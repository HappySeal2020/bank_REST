package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public class UserCardResponseDto {
    @JsonProperty
    private final List<UserCardDto> cards;
    @JsonProperty
    private final BigDecimal totalBalance;

    public UserCardResponseDto(List<UserCardDto> cards, BigDecimal totalBalance) {
        this.cards = cards;
        this.totalBalance = totalBalance;
    }
}
