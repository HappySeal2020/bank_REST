package com.example.bankcards.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MaskingServiceTest {
    @Test
    void shouldMaskCardNumber() {
        MaskingService service = new MaskingService();
        String result = service.maskCardNumber("1234567812345678");
        assertEquals("************5678", result);
    }
}
