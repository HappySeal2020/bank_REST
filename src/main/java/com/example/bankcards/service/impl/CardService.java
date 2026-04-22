package com.example.bankcards.service.impl;

import com.example.bankcards.dto.AdminCardDto;
import com.example.bankcards.dto.UserCardResponseDto;
import com.example.bankcards.entity.Card;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interface for card service
 */
public interface CardService {
    /**
     * Retrieves a card by its id
     * @param id card id
     * @return found card
     */
    Card getById(Long id);

    /**
     * Retrieves user's card by its id
     * @param id card id
     * @return found card
     */
    Card getOwnCardById(Long id);

    /**
     * Admin retrieve cards
     * @param page number of page
     * @param size page size
     * @param card last 4 symbols of card number
     * @param owner card's owner
     * @param login user's login
     * @return list of cards for Admin
     */
    List<AdminCardDto> getCards(int page, int size, String card, String owner, String login);

    /**
     * User retrieve cards
     * @param page number of page
     * @param size page size
     * @param card last 4 symbols of card number
     * @return list of cards for User
     */
    UserCardResponseDto getUserCard(int page, int size, String card);

    /**
     * Client transfers money from one his card to another
     * @param src id of source card
     * @param dest id of destination card
     * @param amount amount to transfer
     * @return amount on destination card after operation
     */
    BigDecimal clientMoneyTransfer(Long src, Long dest, BigDecimal amount);

    /**
     * Client increase or decrease balance
     * @param src id of source card
     * @param amount amount to increase or decrease
     * @return amount on source card after operation
     */
    BigDecimal clientChangeBalance(Long src, BigDecimal amount);

    /**
     * Client make request to lock or unlock card
     * @param id card id to unlock
     * @param action LOCK or UNLOCK
     */
    void clientUpdateCardStatus (long id, String action);

    /**
     * Save new or changed card
     * @param card card id
     * @param doEncrypt make card number's encryption (new card=yes; change balance, transfer, change status=no)
     * @return card after save
     */
    Card save(Card card, boolean doEncrypt);

    /**
     * delete card
     * @param id card id
     */
    void delete(long id);

    /**
     * Admin retrieves list of cards to lock/unlock
     * @return list of cards to lock/unlock
     */
    List<Card> getMismatchStatusCard();

    /**
     * Admin lock/unlock card
     * @param id card
     * @param action lock or unlock
     */
    void adminUpdateCardStatus(long id, String action);

}
