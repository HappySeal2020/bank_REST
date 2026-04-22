package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.bankcards.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Bank card for Admin
 */
@Schema(description="Банковская карта для Админ")
public class AdminCardDto {
    @JsonProperty("id")
    private final long id;
    @JsonProperty("user_id")
    private final User user;
    @JsonProperty("card_num")
    private final String cardNum;
    @JsonProperty("card_num4")
    private final String cardNum4;
    @JsonProperty("owner")
    private final String owner;
    @JsonProperty("valid_thru")
    private final LocalDate validThru;
    @JsonProperty("current_status")
    private final CardStatus currentStatus;
    @JsonProperty("requested_status")
    private final CardStatus requestedStatus;
    @JsonProperty("balance")
    private final BigDecimal balance;

    public AdminCardDto(long id, User user, String cardNum, String cardNum4, String owner, LocalDate validThru, CardStatus currentStatus,
                        CardStatus requestedStatus, BigDecimal balance) {
        this.id = id;
        this.user = user;
        this.cardNum = cardNum;
        this.cardNum4 = cardNum4;
        this.owner = owner;
        this.validThru = validThru;
        this.currentStatus = currentStatus;
        this.requestedStatus = requestedStatus;
        this.balance = balance;

    }
}
