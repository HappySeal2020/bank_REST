package com.example.bankcards.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.security.core.GrantedAuthority;

/**
 * Application roles
 */
@Schema(description="Роль пользователя системы")
public enum Role implements GrantedAuthority {
    ADMIN,
    USER;
    @Override
    public String getAuthority() {
        return "ROLE_"+ name().toUpperCase();
    }

    public String getRole() {
        return name().toUpperCase();
    }

}
