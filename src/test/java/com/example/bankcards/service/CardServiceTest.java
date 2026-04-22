package com.example.bankcards.service;
import com.example.bankcards.dto.UserCardDto;
import com.example.bankcards.dto.UserCardResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardMapper;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CardServiceTest {
    @InjectMocks
    private CardServiceImpl cardService;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardMapper cardMapper;

    @Mock
    private AesServiceImpl aesServiceImpl;

    //запрос на блокировку карты
    @Test
    void shouldRequestToLockCard() {
        Card card = new Card();
        card.setId(1L);
        card.setCardNum("123456789");
        card.setValidThru(LocalDate.now());
        card.setCurrentStatus(CardStatus.ACTIVE);
        card.setRequestedStatus(CardStatus.ACTIVE);
        final String username = "brooke";
        User user = new User();
        user.setId(1L);
        user.setLogin(username);
        card.setUser(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, List.of())
        );
        when(cardRepository.findByIdAndUserLogin(1L, username))
                .thenReturn(Optional.of(card));
        when(cardRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        cardService.clientUpdateCardStatus(1L, "LOCK");
        assertEquals(CardStatus.BLOCKED, card.getRequestedStatus());
        SecurityContextHolder.clearContext();
    }

    //запрос на разблокировку карты
    @Test
    void shouldRequestToActivateCard() {
        Card card = new Card();
        card.setId(1L);
        card.setCardNum("123456789");
        card.setValidThru(LocalDate.now());
        card.setCurrentStatus(CardStatus.BLOCKED);
        card.setRequestedStatus(CardStatus.BLOCKED);
        final String username = "sean";
        User user = new User();
        user.setId(1L);
        user.setLogin(username);
        card.setUser(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, List.of())
        );
        when(cardRepository.findByIdAndUserLogin(1L, username))
                .thenReturn(Optional.of(card));
        when(cardRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        cardService.clientUpdateCardStatus(1L, "ACTIVATE");
        assertEquals(CardStatus.ACTIVE, card.getRequestedStatus());
        SecurityContextHolder.clearContext();
    }

    //запрос на блокировку карты когда карта в неверном статусе
    @Test
    void shouldThrowWhenLockInvalidRequestStatus() {
        Card card = new Card();
        card.setId(1L);
        card.setCardNum("123456789");
        card.setValidThru(LocalDate.now());
        card.setCurrentStatus(CardStatus.BLOCKED);
        card.setRequestedStatus(CardStatus.BLOCKED);
        final String username = "roger";
        User user = new User();
        user.setId(1L);
        user.setLogin(username);
        card.setUser(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, List.of())
        );
        when(cardRepository.findByIdAndUserLogin(1L, username))
                .thenReturn(Optional.of(card));

        Exception exception = assertThrows(RuntimeException.class,
                () -> cardService.clientUpdateCardStatus(1L, "LOCK"));
        assertThat(exception.getMessage(),containsString("NOT changed due to wrong status"));
        SecurityContextHolder.clearContext();
    }

    //запрос на разблокировку карты когда карта в неверном статусе
    @Test
    void shouldThrowWhenActivateInvalidRequestStatus() {
        Card card = new Card();
        card.setId(1L);
        card.setCardNum("123456789");
        card.setValidThru(LocalDate.now());
        card.setCurrentStatus(CardStatus.ACTIVE);
        card.setRequestedStatus(CardStatus.ACTIVE);
        final String username = "roger";
        User user = new User();
        user.setId(1L);
        user.setLogin(username);
        card.setUser(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, List.of())
        );
        when(cardRepository.findByIdAndUserLogin(1L, username))
                .thenReturn(Optional.of(card));

        Exception exception = assertThrows(RuntimeException.class,
                () -> cardService.clientUpdateCardStatus(1L, "ACTIVATE"));
        assertThat(exception.getMessage(),containsString("NOT changed due to wrong status"));
        SecurityContextHolder.clearContext();
    }

    //запрос на блокировку-разблокировку карты -- неверная операция
    @Test
    void shouldThrowWhenUnknownAction() {
        Card card = new Card();
        card.setId(1L);
        card.setCardNum("123456789");
        card.setValidThru(LocalDate.now());
        card.setCurrentStatus(CardStatus.ACTIVE);
        card.setRequestedStatus(CardStatus.ACTIVE);
        final String username = "daniel";
        User user = new User();
        user.setId(1L);
        user.setLogin(username);
        card.setUser(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, List.of())
        );
        when(cardRepository.findByIdAndUserLogin(1L, username))
                .thenReturn(Optional.of(card));
        Exception exception = assertThrows(RuntimeException.class,
                () -> cardService.clientUpdateCardStatus(1L, "INVALID_ACTION"));
        assertThat(exception.getMessage(),containsString("Change card status - unknown action"));
        SecurityContextHolder.clearContext();
    }

    //перевод между своими картами
    @Test
    void shouldClientMoneyTransfer() {
        final String username = "harrison";
        User user = new User();
        user.setId(1L);
        user.setLogin(username);
        Card srcCard = new Card();
        srcCard.setId(1L);
        srcCard.setCardNum("123456789");
        srcCard.setValidThru(LocalDate.now());
        srcCard.setCurrentStatus(CardStatus.ACTIVE);
        srcCard.setRequestedStatus(CardStatus.ACTIVE);
        srcCard.setOwner("MR HARRISON");
        srcCard.setBalance(BigDecimal.valueOf(110));
        srcCard.setUser(user);
        Card destCard = new Card();
        destCard.setId(2L);
        destCard.setCardNum("987654321");
        destCard.setValidThru(LocalDate.now());
        destCard.setCurrentStatus(CardStatus.ACTIVE);
        destCard.setRequestedStatus(CardStatus.ACTIVE);
        destCard.setOwner("MR HARRISON");
        destCard.setBalance(BigDecimal.valueOf(10));
        destCard.setUser(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, List.of())
        );
        when(cardRepository.findByIdAndUserLogin(1L, username))
                .thenReturn(Optional.of(srcCard));
        when(cardRepository.findByIdAndUserLogin(2L, username))
                .thenReturn(Optional.of(destCard));
        when(cardRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        cardService.clientMoneyTransfer(1L, 2L, BigDecimal.valueOf(45));
        assertEquals(BigDecimal.valueOf(65), srcCard.getBalance());
        assertEquals(BigDecimal.valueOf(55), destCard.getBalance());
        SecurityContextHolder.clearContext();
    }

    //перевод между своими картами -- ошибка, нулевая сумма
    @Test
    void shouldThrowZeroAmountClientMoneyTransfer() {
        final String username = "harrison";
        User user = new User();
        user.setId(1L);
        user.setLogin(username);
        Card srcCard = new Card();
        srcCard.setId(1L);
        srcCard.setCardNum("123456789");
        srcCard.setValidThru(LocalDate.now());
        srcCard.setCurrentStatus(CardStatus.ACTIVE);
        srcCard.setRequestedStatus(CardStatus.ACTIVE);
        srcCard.setOwner("MR HARRISON");
        srcCard.setBalance(BigDecimal.valueOf(110));
        srcCard.setUser(user);
        Card destCard = new Card();
        destCard.setId(2L);
        destCard.setCardNum("987654321");
        destCard.setValidThru(LocalDate.now());
        destCard.setCurrentStatus(CardStatus.ACTIVE);
        destCard.setRequestedStatus(CardStatus.ACTIVE);
        destCard.setOwner("MR HARRISON");
        destCard.setBalance(BigDecimal.valueOf(10));
        destCard.setUser(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, List.of())
        );
        Exception exception = assertThrows(RuntimeException.class,
                () -> cardService.clientMoneyTransfer(1L, 2L, BigDecimal.valueOf(0)));
        assertThat(exception.getMessage(),containsString("Amount must be greater than zero"));
        SecurityContextHolder.clearContext();
    }


    //перевод между своими картами -- ошибка, недостаточно средств на карте откуда переводим
    @Test
    void shouldThrowInsufficientFundsClientMoneyTransfer() {
        final String username = "harrison";
        User user = new User();
        user.setId(1L);
        user.setLogin(username);
        Card srcCard = new Card();
        srcCard.setId(1L);
        srcCard.setCardNum("123456789");
        srcCard.setValidThru(LocalDate.now());
        srcCard.setCurrentStatus(CardStatus.ACTIVE);
        srcCard.setRequestedStatus(CardStatus.ACTIVE);
        srcCard.setOwner("MR HARRISON");
        srcCard.setBalance(BigDecimal.valueOf(110));
        srcCard.setUser(user);
        Card destCard = new Card();
        destCard.setId(2L);
        destCard.setCardNum("987654321");
        destCard.setValidThru(LocalDate.now());
        destCard.setCurrentStatus(CardStatus.ACTIVE);
        destCard.setRequestedStatus(CardStatus.ACTIVE);
        destCard.setOwner("MR HARRISON");
        destCard.setBalance(BigDecimal.valueOf(10));
        destCard.setUser(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, List.of())
        );
        when(cardRepository.findByIdAndUserLogin(1L, username))
                .thenReturn(Optional.of(srcCard));
        when(cardRepository.findByIdAndUserLogin(2L, username))
                .thenReturn(Optional.of(destCard));

        Exception exception = assertThrows(RuntimeException.class,
                () -> cardService.clientMoneyTransfer(1L, 2L, BigDecimal.valueOf(500)));
        assertThat(exception.getMessage(),containsString("Insufficient funds on the card"));
        SecurityContextHolder.clearContext();
    }

    //изменение баланса своей карты
    @Test
    void shouldClientChangeBalance() {
        Card card = new Card();
        card.setId(1L);
        card.setCardNum("123456789");
        card.setValidThru(LocalDate.now());
        card.setCurrentStatus(CardStatus.ACTIVE);
        card.setRequestedStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.valueOf(110));
        final String username = "pierce";
        User user = new User();
        user.setId(1L);
        user.setLogin(username);
        card.setUser(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, List.of())
        );
        when(cardRepository.findByIdAndUserLogin(1L, username))
                .thenReturn(Optional.of(card));
        when(cardRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        cardService.clientChangeBalance(1L,BigDecimal.valueOf(300));
        assertEquals(BigDecimal.valueOf(410), card.getBalance());
        SecurityContextHolder.clearContext();
    }

    //изменение баланса своей карты - пустая сумма
    @Test
    void shouldThrowNullAmountClientChangeBalance() {
        Card card = new Card();
        card.setId(1L);
        card.setCardNum("123456789");
        card.setValidThru(LocalDate.now());
        card.setCurrentStatus(CardStatus.ACTIVE);
        card.setRequestedStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.valueOf(110));
        final String username = "pierce";
        User user = new User();
        user.setId(1L);
        user.setLogin(username);
        card.setUser(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, List.of())
        );
        when(cardRepository.findByIdAndUserLogin(1L, username))
                .thenReturn(Optional.of(card));
        Exception exception = assertThrows(RuntimeException.class,
                () -> cardService.clientChangeBalance(1L, null));
        assertThat(exception.getMessage(),containsString("Wrong amount"));
        SecurityContextHolder.clearContext();
    }

    //блокировка карты админом
    @Test
    void shouldAdminLockCardStatus() {
        Card card = new Card();
        card.setId(1L);
        card.setCardNum("123456789");
        card.setValidThru(LocalDate.now());
        card.setCurrentStatus(CardStatus.ACTIVE);
        card.setRequestedStatus(CardStatus.BLOCKED);
        card.setBalance(BigDecimal.valueOf(110));
        final String username = "timothy";
        User user = new User();
        user.setId(1L);
        user.setLogin(username);
        card.setUser(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, List.of())
        );
        when(cardRepository.findById(1L))
                .thenReturn(Optional.of(card));
        when(cardRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        cardService.adminUpdateCardStatus(1L,"lock");
        assertEquals(CardStatus.BLOCKED, card.getCurrentStatus());
        SecurityContextHolder.clearContext();
    }

    //разблокировка карты админом
    @Test
    void shouldAdminActivateCardStatus() {
        Card card = new Card();
        card.setId(1L);
        card.setCardNum("123456789");
        card.setValidThru(LocalDate.now());
        card.setCurrentStatus(CardStatus.BLOCKED);
        card.setRequestedStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.valueOf(110));
        final String username = "timothy";
        User user = new User();
        user.setId(1L);
        user.setLogin(username);
        card.setUser(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, List.of())
        );
        when(cardRepository.findById(1L))
                .thenReturn(Optional.of(card));
        when(cardRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        cardService.adminUpdateCardStatus(1L,"activate");
        assertEquals(CardStatus.ACTIVE, card.getCurrentStatus());
        SecurityContextHolder.clearContext();
    }

    //разблокировка карты админом -- ошибка, не было заявки на блокировку
    @Test
    void shouldThrowNoRequestAdminChangeCardStatus() {
        Card card = new Card();
        card.setId(1L);
        card.setCardNum("123456789");
        card.setValidThru(LocalDate.now());
        card.setCurrentStatus(CardStatus.ACTIVE);
        card.setRequestedStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.valueOf(110));
        final String username = "timothy";
        User user = new User();
        user.setId(1L);
        user.setLogin(username);
        card.setUser(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, List.of())
        );
        when(cardRepository.findById(1L))
                .thenReturn(Optional.of(card));
        Exception exception = assertThrows(RuntimeException.class,
                () ->cardService.adminUpdateCardStatus(1L,"lock"));
        assertThat(exception.getMessage(),matchesPattern("Change card status for id.*was not requested"));
        SecurityContextHolder.clearContext();
    }

    //разблокировка карты админом -- ошибка, неверный статус
    @Test
    void shouldThrowWrongStatusAdminChangeCardStatus() {
        Card card = new Card();
        card.setId(1L);
        card.setCardNum("123456789");
        card.setValidThru(LocalDate.now());
        card.setCurrentStatus(CardStatus.ACTIVE);
        card.setRequestedStatus(CardStatus.BLOCKED);
        card.setBalance(BigDecimal.valueOf(110));
        final String username = "timothy";
        User user = new User();
        user.setId(1L);
        user.setLogin(username);
        card.setUser(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, List.of())
        );
        when(cardRepository.findById(1L))
                .thenReturn(Optional.of(card));
        Exception exception = assertThrows(RuntimeException.class,
                () ->cardService.adminUpdateCardStatus(1L,"activate"));
        assertThat(exception.getMessage(),matchesPattern("Current card status.*NOT changed due to wrong status.*"));
        SecurityContextHolder.clearContext();
    }

    //разблокировка карты админом -- ошибка, неверная команда
    @Test
    void shouldThrowUnknownActionAdminChangeCardStatus() {
        Card card = new Card();
        card.setId(1L);
        card.setCardNum("123456789");
        card.setValidThru(LocalDate.now());
        card.setCurrentStatus(CardStatus.ACTIVE);
        card.setRequestedStatus(CardStatus.BLOCKED);
        card.setBalance(BigDecimal.valueOf(110));
        final String username = "timothy";
        User user = new User();
        user.setId(1L);
        user.setLogin(username);
        card.setUser(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, List.of())
        );
        when(cardRepository.findById(1L))
                .thenReturn(Optional.of(card));
        Exception exception = assertThrows(RuntimeException.class,
                () ->cardService.adminUpdateCardStatus(1L,"INVALID_ACTION"));
        assertThat(exception.getMessage(),containsString("Change card status - unknown action"));
        SecurityContextHolder.clearContext();
    }

    //просмотр карт клиентом + баланс
    @Test
    void shouldReturnCardsAndTotalBalance() {
        String login = "user";

        Card card = new Card();
        Page<Card> page = new PageImpl<>(List.of(card));

        when(cardRepository.findByUserLogin(eq(login), any()))
                .thenReturn(page);

        when(cardMapper.toUserCardDto(card))
                .thenReturn(new UserCardDto());

        when(cardRepository.getTotalBalance(login))
                .thenReturn(BigDecimal.valueOf(1000));

        CardServiceImpl spyService = Mockito.spy(cardService);
        doReturn(login).when(spyService).getUsername();

        UserCardResponseDto response =
                spyService.getUserCard(0, 5, null);

        assertEquals(1, response.getCards().size());
        assertEquals(BigDecimal.valueOf(1000), response.getTotalBalance());
        SecurityContextHolder.clearContext();
    }

    //просмотр карт клиентом + баланс (нет карт)
    @Test
    void shouldReturnNoCardsAndTotalBalance() {
        String login = "user";
        Card card = new Card();
        card.setId(1L);
        card.setCardNum("123456789");
        card.setCardNum4("6789");
        card.setValidThru(LocalDate.now());
        card.setBalance(BigDecimal.valueOf(110));
        Page<Card> page = new PageImpl<>(List.of(card));

        when(cardRepository.findByUserLogin(eq(login), any()))
                .thenReturn(page);

        when(cardMapper.toUserCardDto(card))
                .thenReturn(new UserCardDto());

        when(cardRepository.getTotalBalance(login))
                .thenReturn(null);

        CardServiceImpl spyService = Mockito.spy(cardService);
        doReturn(login).when(spyService).getUsername();

        UserCardResponseDto response =
                spyService.getUserCard(0, 5, null);

        assertEquals(1, response.getCards().size());
        assertEquals(BigDecimal.ZERO, response.getTotalBalance());
        SecurityContextHolder.clearContext();
    }

    //просмотр карт клиентом + баланс (нет карт) - фильтр по карте
    @Test
    void shouldReturnFilterCardsAndTotalBalance() {
        String login = "user";
        Card card = new Card();
        card.setId(1L);
        card.setCardNum("123456789");
        card.setCardNum4("6789");
        card.setValidThru(LocalDate.now());
        card.setBalance(BigDecimal.valueOf(110));
        Page<Card> page = new PageImpl<>(List.of(card));

        when(cardRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        when(cardMapper.toUserCardDto(card))
                .thenReturn(new UserCardDto());

        when(cardRepository.getTotalBalance(login))
                .thenReturn(null);

        CardServiceImpl spyService = Mockito.spy(cardService);
        doReturn(login).when(spyService).getUsername();

        UserCardResponseDto response =
                spyService.getUserCard(0, 5, "6789");

        assertEquals(1, response.getCards().size());
        assertEquals(BigDecimal.ZERO, response.getTotalBalance());
        SecurityContextHolder.clearContext();
    }
    //Сохранение карты с шифрованием
    @Test
    void shouldSaveCardEncrypt(){
        User user = new User();
        user.setId(1L);
        user.setLogin("alain");
        Card card = new Card();
        card.setId(1L);
        card.setCardNum("123456789");
        LocalDate validThru = LocalDate.of(2026,1, 10);
        card.setValidThru(validThru);
        card.setBalance(BigDecimal.valueOf(130));
        card.setCurrentStatus(CardStatus.ACTIVE);
        card.setRequestedStatus(CardStatus.ACTIVE);
        card.setUser(user);
        when (cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(aesServiceImpl.encrypt(anyString()))
                .thenReturn("ENCRYPTED_VALUE_12345678901234567890");
        Card savedCard=cardService.save(card,true);
        assertEquals("6789", savedCard.getCardNum4());
        assertThat(savedCard.getCardNum(),containsString("ENCRYPTED_VALUE_"));
        LocalDate expectedDate = LocalDate.of(2026,1, 31);
        assertEquals(expectedDate, savedCard.getValidThru());
        SecurityContextHolder.clearContext();
    }

    //Сохранение карты без шифрования
    @Test
    void shouldSaveCardNotEncrypt(){
        User user = new User();
        user.setId(1L);
        user.setLogin("alain");
        Card card = new Card();
        card.setId(1L);
        card.setCardNum("123456789");
        LocalDate validThru = LocalDate.of(2026,1, 10);
        card.setValidThru(validThru);
        card.setBalance(BigDecimal.valueOf(130));
        card.setCurrentStatus(CardStatus.ACTIVE);
        card.setRequestedStatus(CardStatus.ACTIVE);
        card.setUser(user);
        when (cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Card savedCard=cardService.save(card,false);
        LocalDate expectedDate = LocalDate.of(2026,1, 31);
        assertEquals(expectedDate, savedCard.getValidThru());
        SecurityContextHolder.clearContext();
    }

    //сохранение карты - ошибка нет пользователя
    @Test
    void shouldThrowNoUserSaveCard(){
        User user = new User();
        user.setId(1L);
        user.setLogin("alain");
        Card card = new Card();
        card.setId(1L);
        card.setCardNum("123456789");
        LocalDate validThru = LocalDate.of(2026,1, 10);
        card.setValidThru(validThru);
        card.setBalance(BigDecimal.valueOf(130));
        card.setCurrentStatus(CardStatus.ACTIVE);
        card.setRequestedStatus(CardStatus.ACTIVE);
        card.setUser(user);

        Exception exception = assertThrows(RuntimeException.class,
                () ->cardService.save(card,false));
        assertThat(exception.getMessage(),containsString("User not found"));
        SecurityContextHolder.clearContext();
    }
}
