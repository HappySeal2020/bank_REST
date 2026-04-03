package com.example.bankcards.service;

import org.springframework.stereotype.Service;

@Service

public class MaskingService {
    public String maskCardNumber(String rawNumber) {
        return rawNumber.substring(0,rawNumber.length()-4).replaceAll("[0-9]","*")
                +rawNumber.substring(rawNumber.length()-4);
    }
}
