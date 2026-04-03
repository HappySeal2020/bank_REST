package com.example.bankcards.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name="card")
public class Card {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
    @NotBlank
    @NotEmpty
    @Column(name = "card_num")
    private String cardNum;
    @NotBlank
    @NotEmpty
    @Column(name = "owner")
    private String owner;
    //@NotBlank
    //@NotEmpty
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
