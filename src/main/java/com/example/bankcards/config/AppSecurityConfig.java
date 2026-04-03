package com.example.bankcards.config;

import com.example.bankcards.entity.Role;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static com.example.bankcards.util.Const.REST_MAP;

@Configuration
public class AppSecurityConfig {
    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(REST_MAP + "/**"))
                .authorizeHttpRequests(auth -> auth
                        //REST
                        //.requestMatchers(HttpMethod.GET, REST_MAP + "/**").hasAnyAuthority(Role.USER.getAuthority(), Role.ADMIN.getAuthority())
                        .requestMatchers(REST_MAP + "/**").permitAll()
                        .requestMatchers(REST_MAP + "/**").hasAnyAuthority(Role.ADMIN.getAuthority())
                        //Grant rules for Actuator
                        .requestMatchers("/actuator/**").authenticated()
                        //Grant rules for Swagger
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/welcome")

                        .hasAnyAuthority(Role.ADMIN.getAuthority(),Role.USER.getAuthority())


                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/welcome", true)
                        .usernameParameter("login")
                        .passwordParameter("password")
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .httpBasic(Customizer.withDefaults()); //For REST
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
