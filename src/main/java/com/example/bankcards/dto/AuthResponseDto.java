package com.example.bankcards.dto;

import lombok.Data;

@Data
public class AuthResponseDto {
    private String accessToken;
    private String refreshToken;

    public AuthResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}
