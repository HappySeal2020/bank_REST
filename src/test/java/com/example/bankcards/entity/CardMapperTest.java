package com.example.bankcards.entity;

import com.example.bankcards.dto.UserCardDto;
import com.example.bankcards.service.AesServiceImpl;
import com.example.bankcards.service.MaskingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CardMapperTest {
    @Mock
    private MaskingService maskingService;

    @Mock
    private AesServiceImpl aesServiceImpl;

    @InjectMocks
    private CardMapper cardMapper;

    @Test
    void shouldMapAndMaskCardNumber() {
        Card card = new Card();
        card.setCardNum("encrypted");
        when(aesServiceImpl.decrypt("encrypted"))
                .thenReturn("1234567812345678");
        when(maskingService.maskCardNumber("1234567812345678"))
                .thenReturn("************5678");
        UserCardDto dto = cardMapper.toUserCardDto(card);
        assertEquals("************5678", dto.getCardNum());
    }
}
