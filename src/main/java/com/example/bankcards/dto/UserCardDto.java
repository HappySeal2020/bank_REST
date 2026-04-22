package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO used when user works with cards
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@Schema(description="Банковская карта для клиента")
public class UserCardDto {
    @JsonProperty("id")
    private long id;
    @JsonProperty("card_num")
    private String cardNum;
    @JsonProperty("card_num4")
    private String cardNum4;
    @JsonProperty("owner")
    private String owner;
    @JsonProperty("valid_thru")
    private LocalDate validThru;
    @JsonProperty("current_status")
    private CardStatus currentStatus;
    @JsonProperty("requested_status")
    private CardStatus requestedStatus;
    @JsonProperty("balance")
    private BigDecimal balance;

    public UserCardDto(long id, String cardNum, String cardNum4, String owner, LocalDate validThru, CardStatus currentStatus,
                       CardStatus requestedStatus, BigDecimal balance) {
        this.id = id;
        this.cardNum = cardNum;
        this.cardNum4 = cardNum4;
        this.owner = owner;
        this.validThru = validThru;
        this.currentStatus = currentStatus;
        this.requestedStatus = requestedStatus;
        this.balance = balance;
    }


}
