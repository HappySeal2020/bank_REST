package com.example.bankcards.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class AesService {
    private final SecretKey secretKey;
    private final Cipher cipher;
    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int IV_SIZE = 16; // 128 bit
    public AesService(@Value("${app.crypto.secret-key}") String base64Key) {
        // Ключ передаётся в конфигурации (например, application.properties)
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        this.secretKey = new SecretKeySpec(decodedKey, "AES");
        try {
            this.cipher = Cipher.getInstance(AES_ALGORITHM);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize cipher", e);
        }
    }
    public String encrypt(String plainText) {
        try {
            // Генерируем случайный инициализационный вектор (IV)
            byte[] iv = new byte[IV_SIZE];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            // Объединяем IV и зашифрованные данные
            byte[] combined = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }
    public String decrypt(String encryptedBase64) {
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedBase64);
            // Извлекаем IV
            byte[] iv = new byte[IV_SIZE];
            System.arraycopy(combined, 0, iv, 0, iv.length);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            // Извлекаем зашифрованные данные
            byte[] encryptedBytes = new byte[combined.length - iv.length];
            System.arraycopy(combined, iv.length, encryptedBytes, 0, encryptedBytes.length);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }


}
