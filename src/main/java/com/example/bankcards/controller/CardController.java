package com.example.bankcards.controller;

import com.example.bankcards.dto.AdminCardDto;
import com.example.bankcards.dto.UserCardResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.CardServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import static com.example.bankcards.util.Const.*;

/**
 * Controller for operations with cards
 */
@Slf4j
@RestController
@RequestMapping(REST_MAP)
public class CardController {
    private final CardServiceImpl cardService;

    public CardController(CardServiceImpl cardService) {
        this.cardService = cardService;
      }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary="Админ читает все карты. Фильтр по последним 4 символам номера карты, по эмбоссированному на карте имени, по логину, пагинация.")
    @GetMapping(REST_CARD)
    @ResponseStatus(HttpStatus.OK)
    public List<AdminCardDto> getCards(@RequestParam(defaultValue = "0") int page, //page number
                                       @RequestParam(defaultValue = "5") int size, //page size
                                       @RequestParam(required = false) String card,  //find by card number
                                       @RequestParam(required = false) String owner,  //find by embossed cardholder name
                                       @RequestParam(required = false) String login  //find by login
    ) {
        return cardService.getCards(page, size, card, owner, login);
    }

    @Operation(summary="Админ создаёт новую карту")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(REST_CARD)
    @ResponseStatus(HttpStatus.CREATED)
    public Card createCard(@Valid @RequestBody Card card) {
        card.setId(0L);
        card.setBalance(BigDecimal.ZERO);
        return cardService.save(card, true);
    }

    @Operation(summary="Админ изменяет карту")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(REST_CARD+"/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Card updateCard(@PathVariable long id, @Valid @RequestBody Card card) {
        if (card.getId() == id) {
            log.info("Update card: {}", card);
            return cardService.save(card, true);
        } else {
            log.warn("Update card SKIPPED: id {} not match to {}", id, card.getId());
            return null;
        }
    }

    @Operation(summary="Админ удаляет карту")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(REST_CARD+"/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(@PathVariable long id) {
        log.info("Delete card: {}", id);
        cardService.delete(id);
    }

//Card status for admin
@Operation(summary="Админ получает список заявок на блокировку, разблокировку карт")
@PreAuthorize("hasRole('ADMIN')")
@GetMapping(REST_CARD_STATUS)
@ResponseStatus(HttpStatus.OK)
public List<Card> getCardsByStatus() {
    return cardService.getMismatchStatusCard();
}

@Operation(summary="Админ выполняет блокировку, разблокировку карт")
@PreAuthorize("hasRole('ADMIN')")
@PutMapping(REST_CARD_STATUS+"/{id}")
@ResponseStatus(HttpStatus.ACCEPTED)
public void updateCardStatus(@PathVariable long id, @RequestParam String action) {
    log.info("Admin change card {} status to {}", id, action);
    cardService.adminUpdateCardStatus(id, action);
}

//--FOR USER--

//See own cards
@Operation(summary="Клиент смотрит свои карты + суммарный баланс. Фильтр по последним 4 символам номера карты, пагинация.")
@GetMapping(REST_CLIENT)
@ResponseStatus(HttpStatus.OK)
    public UserCardResponseDto getCardsByClient(
            @RequestParam(defaultValue = "0") int page, //page number
            @RequestParam(defaultValue = "5") int size, //page size
            @RequestParam(required = false) String card //card number
) {
        return cardService.getUserCard(page, size, card);
}

//Lock request
@Operation(summary="Клиент делает заявку на блокировку, разблокировку своей карты.")
@PutMapping(REST_CLIENT_CARDSTATUS+"/{id}")
@ResponseStatus(HttpStatus.ACCEPTED)
    public void setClientCardStatus(@PathVariable long id, @RequestParam String action) {
        log.info("Set Client Card: {} Status: {}", id, action);
        cardService.clientUpdateCardStatus(id, action);
}
//Transfers between own cards
@Operation(summary="Клиент делает перевод между своими картами")
@PutMapping(REST_CLIENT_TRANSFER)
@ResponseStatus(HttpStatus.ACCEPTED)
public BigDecimal clientMoneyTransfer(@RequestParam Long src, @RequestParam Long dest, @RequestParam BigDecimal amount) {
    log.info("Transfer from card: {} to card: {} amount: {}", src, dest, amount);
    return cardService.clientMoneyTransfer(src, dest, amount);
}

//Replenishment-Debiting of funds
@Operation(summary="Клиент увеличивает или уменьшает баланс своей карты.")
@PutMapping(REST_CLIENT_CHANGEBALANCE)
@ResponseStatus(HttpStatus.ACCEPTED)
public BigDecimal clientChangeBalance(@RequestParam Long src, @RequestParam BigDecimal amount) {
    log.info("Change balance card: {} amount: {}", src, amount);
    return cardService.clientChangeBalance(src, amount);
}


}
