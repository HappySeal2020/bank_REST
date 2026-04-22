package com.example.bankcards.dto;

import lombok.Data;

/**
 * DTO for authentication response
 */
@Data
public class AuthResponseDto {
    private String accessToken;
    private String refreshToken;

    public AuthResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}
