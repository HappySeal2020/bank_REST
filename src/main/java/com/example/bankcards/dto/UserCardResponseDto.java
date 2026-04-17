package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
@Schema(description="Банковская карта для клиента + суммарный баланс по всем картам")
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
