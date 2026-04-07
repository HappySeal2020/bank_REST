package com.example.bankcards.security;

import lombok.Data;

@Data
public class AuthRequest {
    private String login;
    private String password;
}
