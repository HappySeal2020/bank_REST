package com.example.bankcards.controller;

import com.example.bankcards.service.JwtServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
//import org.testcontainers.shaded.com.github.dockerjava.core.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import static com.example.bankcards.util.Const.*;
//import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j

public class ControllerIntegrationTest {
    @Autowired
    private JwtServiceImpl jwtServiceImpl;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserDetailsService userDetailsService;

    @Value("${app.crypto.access-secret}")
    private String ACCESS_SECRET;

    @Test
    void shouldReturnCardsForAdmin() throws Exception {
        UserDetails user = userDetailsService.loadUserByUsername("admin");
        String token = jwtServiceImpl.generateAccessToken(user);
        log.info("User={}, Token={}", user, token);
        mockMvc.perform(get(REST_MAP+REST_CARD)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn403ForUser() throws Exception {
        UserDetails user = userDetailsService.loadUserByUsername("olga");
        String token = jwtServiceImpl.generateAccessToken(user);
        mockMvc.perform(get(REST_MAP+REST_CARD)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    //доступ к Endpoint для User
    @Test
    void userShouldAccessUserEndpoint() throws Exception {
        UserDetails user = userDetailsService.loadUserByUsername("olga");
        String token = jwtServiceImpl.generateAccessToken(user);
        mockMvc.perform(get(REST_MAP+REST_CLIENT)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn403WithoutToken() throws Exception {
        mockMvc.perform(get(REST_MAP+REST_CARD))
                .andExpect(status().isForbidden());
                //.andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401ForInvalidToken() throws Exception {
        mockMvc.perform(get(REST_MAP + REST_CARD)
                        .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized()); // или 403 — зависит от конфигурации
    }

    @Test
    void shouldReturn401ForExpiredToken() throws Exception {
        UserDetails user = userDetailsService.loadUserByUsername("admin");
        String expiredToken = jwtServiceImpl.generateToken(
                user,
                -1000, // уже истёк
                ACCESS_SECRET,
                "access"
        );

        mockMvc.perform(get(REST_MAP + REST_CARD)
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }
    //неправильный тип токена
    @Test
    void shouldRejectRefreshTokenForProtectedEndpoint() throws Exception {
        UserDetails user = userDetailsService.loadUserByUsername("admin");
        String refreshToken = jwtServiceImpl.generateRefreshToken(user);
        mockMvc.perform(get(REST_MAP + REST_CARD)
                        .header("Authorization", "Bearer " + refreshToken))
                .andExpect(status().isUnauthorized());
    }

    //нет роли
    @Test
    void shouldReturn403IfNoRolesInToken() throws Exception {
        UserDetails user = userDetailsService.loadUserByUsername("admin");
        String token = Jwts.builder()
                .setSubject(user.getUsername())
                //.claim("roles", roles) // ❌ без roles
                .claim("type", "access")
                .claim("jti", UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .signWith( Keys.hmacShaKeyFor(ACCESS_SECRET.getBytes(StandardCharsets.UTF_8))  , SignatureAlgorithm.HS256)
                .compact();
        mockMvc.perform(get(REST_MAP + REST_CARD)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }


    //Test login endpoint
    @Test
    void shouldLoginAndGetToken() throws Exception {
        mockMvc.perform(post(AUTH_PATH+REST_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "login": "admin",
                          "password": "admin"
                        }
                    """))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("accessToken"));
    }

    //Test chain: login --> access
    @Test
    void fullFlow_shouldLoginAndAccessProtectedEndpoint() throws Exception {
        var loginResult = mockMvc.perform(post(AUTH_PATH + REST_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "login": "admin",
                      "password": "admin"
                    }
                """))
                .andReturn();
        var cookies = loginResult.getResponse().getCookies();
        mockMvc.perform(get(REST_MAP + REST_CARD)
                        .cookie(cookies))
                .andExpect(status().isOk());
    }
}
