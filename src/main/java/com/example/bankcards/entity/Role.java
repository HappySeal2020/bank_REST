package com.example.bankcards.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Role  {
    ADMIN,
    USER;
    //@Override
    public String getAuthority() {
        return "ROLE_"+ name().toUpperCase();
    }
}
