package com.example.bankcards.config;

import com.example.bankcards.entity.Role;
import com.example.bankcards.exception.JwtAccessDeniedHandler;
import com.example.bankcards.exception.JwtAuthenticationEntryPoint;
import com.example.bankcards.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.example.bankcards.util.Const.*;

@Configuration
@EnableMethodSecurity
public class AppSecurityConfig {
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    JwtAccessDeniedHandler jwtAccessDeniedHandler;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        //login
                        .requestMatchers(AUTH_PATH+REST_LOGIN).permitAll()
                        .requestMatchers(AUTH_PATH+REST_REFRESH).permitAll()

                        // ADMIN
                        .requestMatchers(REST_MAP + REST_CARD + "/**")
                        .hasAuthority(Role.ADMIN.getAuthority())
                        .requestMatchers(REST_MAP + REST_USER + "/**")
                        .hasAuthority(Role.ADMIN.getAuthority())

                        // USER
                        .requestMatchers(REST_MAP + REST_CLIENT + "/**")
                        .hasAuthority(Role.USER.getAuthority())

                        // прочее
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/actuator/**").authenticated()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
