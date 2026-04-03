package com.example.bankcards.service;

import com.example.bankcards.dto.AdminCardDto;
import com.example.bankcards.dto.UserCardDto;
import com.example.bankcards.dto.UserCardResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardMapper;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;
    private final AesService aesService;
    private final MaskingService maskingService;

    //READ
    public Card getById(Long id) {

        return cardRepository.findById(id)
                .map(c -> {
                    String encrypted = c.getCardNum();
                    String decrypted= aesService.decrypt(encrypted);
                    String masked = maskingService.maskCardNumber(decrypted);
                    c.setCardNum(masked);
                return c;
                })
                .orElseThrow(() ->
                        new NotFoundException("Card not found id=" + id));
    }
    public Card getOwnCardById(Long id) {
        return cardRepository.findOwnCards(getUsername()).stream()
                .filter(c-> c.getId() ==id)
                .findAny()
                .orElseThrow(() -> new NotFoundException("Card not found id="+id));
    }

    public List<AdminCardDto> getAllCards() {
        List<Card> cards = cardRepository.findAll();
        return cards.stream()
                .map(cardMapper::toDto)
                .toList();
    }

    //Clients

    //User cards + balance
    public UserCardResponseDto getUserCard() {
        String login=getUsername();
        List<Card> cards = cardRepository.findOwnCards(login);
        log.info("Cards owned by {} - {}",login , cards);
        List<UserCardDto> dtos = cards.stream()
                .map(cardMapper::toUserCardDto)
                .toList();
        BigDecimal total = cardRepository.getTotalBalance(login);
        if (total == null) { total = new BigDecimal(0); }
        return new UserCardResponseDto(dtos, total);
    }

    //Money transfer between own cards
    @Transactional
    public BigDecimal clientMoneyTransfer(Long src, Long dest, BigDecimal amount){
        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NotFoundException("Amount must be greater than zero");
        }
        Card srcCard = getOwnCardById(src);
        Card destCard = getOwnCardById(dest);
        if(srcCard.getBalance().compareTo(amount) > 0) {
            srcCard.setBalance(srcCard.getBalance().subtract(amount));
            destCard.setBalance(destCard.getBalance().add(amount));
            log.info("Transfer amount={} from {} to {}", amount, srcCard, destCard);
            save(srcCard, false);
            save(destCard, false);
            return destCard.getBalance();
        } else {
            String msg="Insufficient funds on the card";
            log.warn(msg);
            throw new NotFoundException(msg);
        }
    }


    //Replenishment-Debiting of funds
    @Transactional
    public BigDecimal clientChangeBalance(Long src, BigDecimal amount){
        Card srcCard = getOwnCardById(src);
        if(amount == null || (amount.add(srcCard.getBalance()).compareTo(BigDecimal.ZERO)) < 0) {
            throw new NotFoundException("Wrong amount");
        }
        srcCard.setBalance(srcCard.getBalance().add(amount));
        log.info("Card {} change balance by amount={} to {}", src, amount, srcCard.getBalance());
        save(srcCard, false);
        return srcCard.getBalance();
    }

    //Client Lock/UnLock requests
    @Transactional
    public void clientUpdateCardStatus (long id, String action) {
        Card card = getOwnCardById(id);
        String msg="Requested card status "+action +" NOT changed due to wrong status "+card;
        switch (action.toUpperCase()) {
            case "LOCK":
                if (card.getRequestedStatus().equals(CardStatus.ACTIVE)) {
                    card.setRequestedStatus(CardStatus.BLOCKED);
                    log.info("Change card status to request BLOCKED {}", card);
                } else {
                    log.warn(msg);
                    throw new RuntimeException(msg);
                }
                break;
            case "ACTIVATE":
                if (card.getRequestedStatus().equals(CardStatus.BLOCKED)) {
                    card.setRequestedStatus(CardStatus.ACTIVE);
                    log.info("Change card status to request ACTIVE {}", card);
                } else {
                    log.warn(msg);
                    throw new RuntimeException(msg);
                }
                break;
            default:
                throw new RuntimeException(String.format("Change card status - unknown action: %s", action));
        }
        cardRepository.save(card);
    }






    //-----ADMIN ------- CREATE card
    @Transactional
    public Card save(Card card, boolean doEncrypt) {
        if (card.getUser() != null && userRepository.findById(card.getUser().getId()).isPresent()) {
            User user = userRepository.findById(card.getUser().getId()).get();
            card.setUser(user);
            //Card valid up to last day of month
            card.setValidThru(card.getValidThru().with(TemporalAdjusters.lastDayOfMonth()));
            if (doEncrypt) {
                String cardNum=card.getCardNum();
                card.setCardNum(aesService.encrypt(cardNum));
            }
            log.info("Save card: {}", card);
            return cardRepository.save(card);
        } else {
            throw new NotFoundException("User not found");
        }
    }

    //DELETE
    @Transactional
    public void delete(long id) {
        cardRepository.deleteById(id);
    }

    //Get cards with mismatch status
    public List<Card> getMismatchStatusCard() {
        return cardRepository.findMismatched();
    }



    @Transactional
    public void adminUpdateCardStatus(long id, String action) {
        Card card = getById(id);
        if (card.getCurrentStatus().equals(card.getRequestedStatus())){
            throw new NotFoundException(String.format("Change card status for id %d was not requested", id));
        }
        String msg="Current card status "+action +" NOT changed due to wrong status "+ card;
        switch (action.toUpperCase()) {
            case "LOCK":
                if (card.getRequestedStatus().equals(CardStatus.BLOCKED)) {
                    card.setCurrentStatus(CardStatus.BLOCKED);
                    log.info("Change card status to BLOCKED {}", card);
                } else {
                    log.warn(msg);
                    throw new RuntimeException(msg);
                }
                break;
            case "ACTIVATE":
                if (card.getRequestedStatus().equals(CardStatus.ACTIVE)) {
                    card.setCurrentStatus(CardStatus.ACTIVE);
                    log.info("Change card status to ACTIVE {}", card);
                } else {
                    log.warn(msg);
                    throw new RuntimeException(msg);
                }
                break;
            default:
                log.warn("Update card status - unknown action: {}", action);
                throw new RuntimeException(String.format("Change card status - unknown action: %s", action));
        }
        cardRepository.save(card);
    }

    public String getUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString(); //anonymous user
        }
    }
}
