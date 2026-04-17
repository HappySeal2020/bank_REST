package com.example.bankcards.service;

import com.example.bankcards.dto.AdminCardDto;
import com.example.bankcards.dto.UserCardDto;
import com.example.bankcards.dto.UserCardResponseDto;
import com.example.bankcards.dto.specification.CardSpecification;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardMapper;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    //private final MaskingService maskingService;

    //READ
    public Card getById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Card not found id=" + id));
    }

    public Card getOwnCardById(Long id){
        String username=getUsername();
        log.info("getOwnCardById: username={}, card id={}",username, id);
        return cardRepository.findByIdAndUserLogin(id, username)
                .orElseThrow(() -> new NotFoundException("Card not found id=" + id));
    }

     public List<AdminCardDto> getCards(int page, int size, String card, String owner, String login) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        Page<Card>cardsPage;
        if ((card != null && !card.isBlank()) ||
        (owner != null && !owner.isBlank()) ||
        (login != null && !login.isBlank())) {
            cardsPage=cardRepository.findAll(CardSpecification.filter(card, owner, login), pageable);
        } else {
            cardsPage=cardRepository.findAll(pageable);
        }
        return cardsPage.map(cardMapper::toDto).getContent();
    }

    //Clients

    public UserCardResponseDto getUserCard(int page, int size, String card) {
        String login = getUsername();
        log.info("Try to get user cards for login {} - page {}, page size {}, card {}", login, page, size, card);
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        Page<Card> pageResult;
        if (card != null && !card.isBlank()) {
            pageResult = cardRepository.findAll(CardSpecification.filter(card, null, login), pageable);
        } else {
            pageResult = cardRepository.findByUserLogin(login, pageable);
        }
        List<UserCardDto> dtos = pageResult.stream()
                .map(cardMapper::toUserCardDto)
                .toList();
        BigDecimal total = cardRepository.getTotalBalance(login);
        if (total == null) total = BigDecimal.ZERO;
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
        log.info("Call getOwnCardById id={}", id);
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
        save(card, false);
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
                card.setCardNum4(card.getCardNum().substring(card.getCardNum().length() - 4));
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
        save(card,false);
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
