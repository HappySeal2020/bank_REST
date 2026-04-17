package com.example.bankcards.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AesServiceTest {
    @Value("${app.crypto.secret-key}")
    private String ACCESS_SECRET;

    @Test
    void shouldEncryptAndDecrypt() {
        System.out.println(ACCESS_SECRET);
        AesService aesService = new AesService(ACCESS_SECRET);
        String raw = "1234567812345678";
        String encrypted = aesService.encrypt(raw);
        String decrypted = aesService.decrypt(encrypted);
        assertEquals(raw, decrypted);
    }
}
