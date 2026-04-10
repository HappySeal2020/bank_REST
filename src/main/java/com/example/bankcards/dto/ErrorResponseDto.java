package com.example.bankcards.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponseDto {
    public ErrorResponseDto(String errorCode, String errorMessage, LocalDateTime timestamp) {
        error = errorCode;
        message = errorMessage;
        this.timestamp = timestamp;
    }
    private String error;
    private String message;
    private LocalDateTime timestamp;
}
