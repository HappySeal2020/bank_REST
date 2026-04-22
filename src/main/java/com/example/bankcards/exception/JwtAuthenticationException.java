package com.example.bankcards.exception;

/**
 * JWT authentication exception
  */
public class JwtAuthenticationException extends RuntimeException {
    public JwtAuthenticationException(String message) {
        super(message);
    }
}