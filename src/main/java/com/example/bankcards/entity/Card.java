package com.example.bankcards.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Card entity
 */
@Data
@Entity

@Table(name="card")
@Schema(description="Банковская карта")
public class Card extends BaseEntity{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
    @NotBlank
    @NotEmpty
    @NotNull
    @Column(name = "card_num")
    private String cardNum;
    @Column(name = "card_num4")
    private String cardNum4;
    @NotBlank
    @NotEmpty
    @NotNull
    @Column(name = "owner")
    private String owner;
    //@NotBlank
    @NotNull
    @Column(name = "valid_thru")
    private LocalDate validThru;
    //@NotBlank
    //@NotEmpty
    @Column(name = "current_status")
    @Enumerated(EnumType.STRING)
    private CardStatus currentStatus;
    //@NotBlank
    //@NotEmpty
    @Column(name = "requested_status")
    @Enumerated(EnumType.STRING)
    private CardStatus requestedStatus;
    //@NotBlank
    //@NotEmpty
    @Column(name = "balance")
    private BigDecimal balance;
}
