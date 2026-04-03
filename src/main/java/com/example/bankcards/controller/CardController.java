package com.example.bankcards.controller;

import com.example.bankcards.dto.AdminCardDto;
import com.example.bankcards.dto.UserCardResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import static com.example.bankcards.util.Const.*;

@Slf4j
@RestController
@RequestMapping(REST_MAP)
public class CardController {
    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    //test only
    @GetMapping(REST_CARD)
    @ResponseStatus(HttpStatus.OK)
    public List<AdminCardDto> getCards() {
        return cardService.getAllCards();
    }

    //Card CRUD for admin
    @PostMapping(REST_CARD)
    @ResponseStatus(HttpStatus.CREATED)
    public Card createCard(@Valid @RequestBody Card card) {
        card.setId(0L);
        card.setBalance(BigDecimal.ZERO);
        return cardService.save(card, true);
    }

    @PutMapping(REST_CARD+"/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Card updateCard(@PathVariable Long id, @Valid @RequestBody Card card) {
        if (card.getId() == id) {
            log.info("Update card: {}", card);
            return cardService.save(card, true);
        } else {
            log.warn("Update card SKIPPED: id {} not match to {}", id, card.getId());
            return null;
        }
    }

    @DeleteMapping(REST_CARD+"/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(@PathVariable long id) {
        log.info("Delete card: {}", id);
        cardService.delete(id);
    }

//Card status for admin
@GetMapping(REST_CARD_STATUS)
@ResponseStatus(HttpStatus.OK)
public List<Card> getCardsByStatus() {
    return cardService.getMismatchStatusCard();
}
@PutMapping(REST_CARD_STATUS+"/{id}")
@ResponseStatus(HttpStatus.ACCEPTED)
public void updateCardStatus(@PathVariable long id, @RequestParam String action) {
    cardService.adminUpdateCardStatus(id, action);
}

//--FOR USER--

//See own cards
@GetMapping(REST_CLIENT)
@ResponseStatus(HttpStatus.OK)
    public UserCardResponseDto getCardsByClient() {
        return cardService.getUserCard();
}
//Lock request
@PutMapping(REST_CLIENT_CARDSTATUS+"/{id}")
@ResponseStatus(HttpStatus.ACCEPTED)
    public void setClientCardStatus(@PathVariable long id, @RequestParam String action) {
        cardService.clientUpdateCardStatus(id, action);
}
//Transfers between own cards
@PutMapping(REST_CLIENT_TRANSFER)
@ResponseStatus(HttpStatus.ACCEPTED)
public BigDecimal clientMoneyTransfer(@RequestParam Long src, @RequestParam Long dest, @RequestParam BigDecimal amount) {
    return cardService.clientMoneyTransfer(src, dest, amount);
}

//Replenishment-Debiting of funds
@PutMapping(REST_CLIENT_CHANGEBALANCE)
@ResponseStatus(HttpStatus.ACCEPTED)
public BigDecimal clientChangeBalance(@RequestParam Long src, @RequestParam BigDecimal amount) {
    return cardService.clientChangeBalance(src, amount);
}


}
