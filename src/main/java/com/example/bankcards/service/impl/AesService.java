package com.example.bankcards.service.impl;

/**
 * Interface for AesService
 */
public interface AesService {
    /**
     * Decrypt message
     * @param encryptedBase64 coded string
     * @return decoded string
     */
    String decrypt(String encryptedBase64);

    /**
     * Encrypt message
     * @param plainText not coded string
     * @return coded string
     */
    String encrypt(String plainText);
}
