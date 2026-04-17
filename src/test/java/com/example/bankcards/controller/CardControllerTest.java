package com.example.bankcards.controller;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.JwtAuthFilter;
import com.example.bankcards.service.CardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import static com.example.bankcards.util.Const.*;
import org.springframework.http.MediaType;

@WebMvcTest(CardController.class)
@AutoConfigureMockMvc(addFilters = false)
@EnableAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class
})
public class CardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnCardsWithDefaultParams() throws Exception {
        when(cardService.getCards(0, 5, null, null, null))
                .thenReturn(List.of());
        mockMvc.perform(get(REST_MAP+REST_CARD))
                .andExpect(status().isOk());
        verify(cardService).getCards(0, 5, null, null, null);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnFilteredCards() throws Exception {
        when(cardService.getCards(1, 10, "1234", "john", "john_login"))
                .thenReturn(List.of());

        mockMvc.perform(get(REST_MAP+REST_CARD)
                        .param("page", "1")
                        .param("size", "10")
                        .param("card", "1234")
                        .param("owner", "john")
                        .param("login", "john_login"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(cardService).getCards(1, 10, "1234", "john", "john_login");
    }

    @Test
    void shouldAddCard() throws Exception {
        User user = new User();
        user.setId(1);
        user.setLogin("john_login");
        user.setRole(Role.USER);
        user.setPassword("{noop}123456");
        Card savedCard = new Card();
        savedCard.setId(1);
        savedCard.setCardNum("1234567890904321");
        savedCard.setOwner("MR JOHN");
        savedCard.setUser(user);
        when(cardService.save(any(Card.class), eq(true)))
                .thenReturn(savedCard);

        mockMvc.perform(post(REST_MAP+REST_CARD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                             "user": { "id": 1},
                             "cardNum": "1234567890904321",
                             "owner": "MR JOHN",
                             "validThru": "2035-12-23",
                             "currentStatus": "ACTIVE",
                             "requestedStatus": "ACTIVE",
                             "balance": 0 }
            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.owner").value("MR JOHN"));

    }

    @Test
    void shouldUpdateCard() throws Exception {
        User user = new User();
        user.setId(1);
        user.setLogin("brooke");
        user.setRole(Role.USER);
        user.setPassword("{noop}123456");
        Card updatedCard = new Card();
        updatedCard.setId(1);
        updatedCard.setCardNum("9999888877776666");
        updatedCard.setOwner("MR BROOKE");
        updatedCard.setUser(user);
        LocalDate givenValidThru = LocalDate.of(2030, 1, 1);
        updatedCard.setValidThru(givenValidThru);
        when(cardService.save(any(Card.class), eq(true)))
        .thenReturn(updatedCard);
        mockMvc.perform(put(REST_MAP+REST_CARD+"/1")
        .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                             "id": 1,
                             "user": { "id": 1},
                             "cardNum": "9999888877776666",
                             "owner": "MR BROOKE",
                             "validThru": "2030-01-01",
                             "currentStatus": "ACTIVE",
                             "requestedStatus": "ACTIVE",
                             "balance": 0
                }
 """
                ))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.owner").value("MR BROOKE"));
    }

    @Test
    void shouldDeleteCard() throws Exception {
        long id = 1L;
        doNothing().when(cardService).delete(id);
        mockMvc.perform(delete(REST_MAP+REST_CARD+"/1"))
            .andExpect(status().isNoContent());
        verify(cardService, times(1)).delete(id);
    }

    @Test
    void shouldReturnValidationErrorOnCardEmpty() throws Exception {
        User user = new User();
        user.setId(1);
        user.setLogin("brooke");
        user.setRole(Role.USER);
        user.setPassword("{noop}123456");
        Card updatedCard = new Card();
        updatedCard.setId(1);
        updatedCard.setOwner("MR BROOKE");
        updatedCard.setUser(user);
        LocalDate givenValidThru = LocalDate.of(2030, 1, 1);
        updatedCard.setValidThru(givenValidThru);
        when(cardService.save(any(Card.class), eq(true)))
                .thenReturn(updatedCard);
        mockMvc.perform(put(REST_MAP+REST_CARD+"/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                             "id": 1,
                             "user": { "id": 1},
                             "owner": "MR BROOKE",
                             "validThru": "2030-01-01",
                             "currentStatus": "ACTIVE",
                             "requestedStatus": "ACTIVE",
                             "balance": 0
                }
 """
                        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.cardNum", anyOf(is("must not be empty"),
                        is("must not be blank"),
                        is("must not be null"))));
    }

    @Test
    void shouldReturnValidationErrorOnCardBlank() throws Exception {
        User user = new User();
        user.setId(1);
        user.setLogin("brooke");
        user.setRole(Role.USER);
        user.setPassword("{noop}123456");
        Card updatedCard = new Card();
        updatedCard.setId(1);
        updatedCard.setOwner("MR BROOKE");
        updatedCard.setUser(user);
        updatedCard.setCardNum("");
        LocalDate givenValidThru = LocalDate.of(2030, 1, 1);
        updatedCard.setValidThru(givenValidThru);
        when(cardService.save(any(Card.class), eq(true)))
                .thenReturn(updatedCard);
        mockMvc.perform(put(REST_MAP+REST_CARD+"/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                             "id": 1,
                             "user": { "id": 1},
                             "owner": "MR BROOKE",
                             "cardNum": "",
                             "validThru": "2030-01-01",
                             "currentStatus": "ACTIVE",
                             "requestedStatus": "ACTIVE",
                             "balance": 0
                }
 """
                        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.cardNum", anyOf(is("must not be empty"),
                        is("must not be blank"),
                        is("must not be null"))));
    }

    @Test
    void shouldReturnValidationErrorOnOwnerEmpty() throws Exception {
        User user = new User();
        user.setId(1);
        user.setLogin("brooke");
        user.setRole(Role.USER);
        user.setPassword("{noop}123456");
        Card updatedCard = new Card();
        updatedCard.setId(1);
        //updatedCard.setOwner("MR BROOKE");
        updatedCard.setCardNum("9999888877776666");
        updatedCard.setUser(user);
        LocalDate givenValidThru = LocalDate.of(2030, 1, 1);
        updatedCard.setValidThru(givenValidThru);
        when(cardService.save(any(Card.class), eq(true)))
                .thenReturn(updatedCard);
        mockMvc.perform(put(REST_MAP+REST_CARD+"/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                             "id": 1,
                             "user": { "id": 1},
                             "cardNum": "9999888877776666",
                             "validThru": "2030-01-01",
                             "currentStatus": "ACTIVE",
                             "requestedStatus": "ACTIVE",
                             "balance": 0
                }
 """
                        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.owner",anyOf(is("must not be empty"),
                        is("must not be blank"),
                        is("must not be null"))));
    }
    @Test
    void shouldReturnValidationErrorOnOwnerBlank() throws Exception {
        User user = new User();
        user.setId(1);
        user.setLogin("brooke");
        user.setRole(Role.USER);
        user.setPassword("{noop}123456");
        Card updatedCard = new Card();
        updatedCard.setId(1);
        //updatedCard.setOwner("MR BROOKE");
        updatedCard.setCardNum("9999888877776666");
        updatedCard.setUser(user);
        LocalDate givenValidThru = LocalDate.of(2030, 1, 1);
        updatedCard.setValidThru(givenValidThru);
        when(cardService.save(any(Card.class), eq(true)))
                .thenReturn(updatedCard);
        mockMvc.perform(put(REST_MAP+REST_CARD+"/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                             "id": 1,
                             "user": { "id": 1},
                             "cardNum": "9999888877776666",
                             "owner": "",
                             "validThru": "2030-01-01",
                             "currentStatus": "ACTIVE",
                             "requestedStatus": "ACTIVE",
                             "balance": 0
                }
 """
                        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.owner",anyOf(is("must not be empty"),
                        is("must not be blank"),
                        is("must not be null"))));
    }

    @Test
    void shouldReturnValidationErrorOnValidThruNull() throws Exception {
        User user = new User();
        user.setId(1);
        user.setLogin("brooke");
        user.setRole(Role.USER);
        user.setPassword("{noop}123456");
        Card updatedCard = new Card();
        updatedCard.setId(1);
        updatedCard.setOwner("MR BROOKE");
        updatedCard.setCardNum("9999888877776666");
        updatedCard.setUser(user);
        when(cardService.save(any(Card.class), eq(true)))
                .thenReturn(updatedCard);
        mockMvc.perform(put(REST_MAP+REST_CARD+"/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                             "id": 1,
                             "user": { "id": 1},
                             "cardNum": "9999888877776666",
                             "owner": "MR BROOKE",
                             "currentStatus": "ACTIVE",
                             "requestedStatus": "ACTIVE",
                             "balance": 0
                }
 """
                        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validThru",anyOf(is("must not be null"))));
    }

    @Test
    void shouldReturnValidationErrorOnValidThruWrong() throws Exception {
        User user = new User();
        user.setId(1);
        user.setLogin("brooke");
        user.setRole(Role.USER);
        user.setPassword("{noop}123456");
        Card updatedCard = new Card();
        updatedCard.setId(1);
        updatedCard.setOwner("MR BROOKE");
        updatedCard.setCardNum("9999888877776666");
        updatedCard.setUser(user);
        when(cardService.save(any(Card.class), eq(true)))
                .thenReturn(updatedCard);
        mockMvc.perform(put(REST_MAP+REST_CARD+"/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                             "id": 1,
                             "user": { "id": 1},
                             "cardNum": "9999888877776666",
                             "owner": "MR BROOKE",
                             "validThru": "2030.01.01",
                             "currentStatus": "ACTIVE",
                             "requestedStatus": "ACTIVE",
                             "balance": 0
                }
 """
                        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",anyOf(is("Invalid date format. Use yyyy-MM-dd"))));
    }

    //clientUpdateCardStatus
    @Test
    void shouldCallServiceToLockCard() throws Exception {
        mockMvc.perform(put(REST_MAP + REST_CLIENT_CARDSTATUS + "/1")
                        .param("action", "LOCK"))
                .andExpect(status().isAccepted());

        verify(cardService).clientUpdateCardStatus(1L, "LOCK");
    }
    @Test
    void shouldClientMoneyTransfer() throws Exception {
        mockMvc.perform(put(REST_MAP + REST_CLIENT_TRANSFER)
                        .param("src", "1")
                        .param("dest", "2")
                        .param("amount", "10"))
                .andExpect(status().isAccepted());
        BigDecimal amount = new BigDecimal(10);
        verify(cardService).clientMoneyTransfer(1L, 2L, amount);
    }

    @Test
    void shouldClientChangeBalance() throws Exception {
       mockMvc.perform(put(REST_MAP + REST_CLIENT_CHANGEBALANCE)
                       .param("src", "1")
                       .param("amount", "10"))
               .andExpect(status().isAccepted());
       BigDecimal amount = new BigDecimal(10);
       verify(cardService).clientChangeBalance(1L, amount);
   }
   @Test
   void shouldGetUserCard() throws Exception {
       mockMvc.perform(get(REST_MAP + REST_CLIENT)
               .param("page", "0")
               .param("size", "10"))
               .andExpect(status().isOk());
       verify(cardService).getUserCard(0, 10, null);
   }

    @Test
    void shouldAdminUpdateCardStatus() throws Exception {
        mockMvc.perform(put(REST_MAP + REST_CARD_STATUS+"/1")
                        .param("action", "LOCK"))
                .andExpect(status().isAccepted());
        verify(cardService).adminUpdateCardStatus(1L, "LOCK");
    }


}
