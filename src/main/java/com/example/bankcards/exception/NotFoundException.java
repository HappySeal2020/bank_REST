package com.example.bankcards.exception;

/**
 * cases when the required data is not found
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

}
