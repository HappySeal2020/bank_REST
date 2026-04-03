package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UserCardDto {
    @JsonProperty("id")
    private final long id;
    //@JsonProperty("user_id")
    //private final User user;
    @JsonProperty("card_num")
    private final String cardNum;
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

    public UserCardDto(long id, String cardNum, String owner, LocalDate validThru, CardStatus currentStatus,
                       CardStatus requestedStatus, BigDecimal balance) {
        this.id = id;
        this.cardNum = cardNum;
        this.owner = owner;
        this.validThru = validThru;
        this.currentStatus = currentStatus;
        this.requestedStatus = requestedStatus;
        this.balance = balance;

    }

}
