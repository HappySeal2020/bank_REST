package com.example.bankcards.controller;

import com.example.bankcards.dto.AuthRequestDto;
import com.example.bankcards.dto.AuthResponseDto;
import com.example.bankcards.exception.JwtAuthenticationException;
import com.example.bankcards.service.JwtService;
import com.example.bankcards.security.RefreshTokenStore;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import static com.example.bankcards.util.Const.*;

@Slf4j
@RestController
@RequestMapping(AUTH_PATH)
public class AuthController {
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenStore refreshTokenStore;

    public AuthController(AuthenticationManager authManager,
                          JwtService jwtService,
                          UserDetailsService userDetailsService,
                          RefreshTokenStore refreshTokenStore) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.refreshTokenStore = refreshTokenStore;
    }

    @Operation(summary="Аутентификация пользователя, Access token")
    @PostMapping(REST_LOGIN)
    public ResponseEntity<?> login(@RequestBody AuthRequestDto request,
                                   HttpServletResponse response) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(),
                        request.getPassword()
                )
        );
        UserDetails user = userDetailsService.loadUserByUsername(request.getLogin());
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        String jti = jwtService.extractJti(refreshToken);
        refreshTokenStore.save(user.getUsername(),jti);
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(false) // true для HTTPS
                .path("/")
                .maxAge(60 * 15)
                //.maxAge(10)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(60 * 60 * 24 * 7)
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        return ResponseEntity.ok().build();
    }

    @Operation(summary="Аутентификация пользователя, Refresh token")
    @PostMapping(REST_REFRESH)
    //public AuthResponseDto refresh(@RequestBody Map<String, String> request)
    public AuthResponseDto refresh(HttpServletRequest request,
                                   HttpServletResponse response) {
        String refreshToken = getCookie(request, "refreshToken");

        if (refreshToken == null) {
            throw new RuntimeException("Refresh token not found");
        }

        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String username = jwtService.extractUsername(refreshToken);
        String jti = jwtService.extractJti(refreshToken);

        // проверка rotation
        if (!refreshTokenStore.isValid(username, jti)) {
            throw new JwtAuthenticationException("Refresh token reused");
        }

        UserDetails user = userDetailsService.loadUserByUsername(username);

        // генерируем НОВЫЕ токены
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        // сохраняем новый jti (старый становится невалидным)
        String newJti = jwtService.extractJti(newRefreshToken);
        refreshTokenStore.save(username, newJti);

        // 🔥 кладём новые cookie
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", newAccessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(60 * 15)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/auth/refresh")
                .maxAge(60 * 60 * 24 * 7)
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
        return new AuthResponseDto(newAccessToken, newRefreshToken);
    }

    private String getCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
