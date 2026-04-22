package com.example.bankcards.entity;

import com.example.bankcards.dto.AdminCardDto;
import com.example.bankcards.dto.UserCardDto;
import com.example.bankcards.service.AesServiceImpl;
import com.example.bankcards.service.MaskingService;
import org.springframework.stereotype.Component;

/**
 * Converts Card for Admin and User
 */
@Component
public class CardMapper {
    private final MaskingService maskingService;
    private final AesServiceImpl aesServiceImpl;

    public CardMapper(MaskingService maskingService, AesServiceImpl aesServiceImpl) {
        this.maskingService = maskingService;
        this.aesServiceImpl = aesServiceImpl;
    }
    public AdminCardDto toDto(Card card) {
        String encrypted = card.getCardNum();
        String decrypted = aesServiceImpl.decrypt(encrypted);
        String masked = maskingService.maskCardNumber(decrypted);
        return new AdminCardDto(card.getId(),
                card.getUser(),
                masked,
                card.getCardNum4(),
                card.getOwner(),
                card.getValidThru(),
                card.getCurrentStatus(),
                card.getRequestedStatus(),
                card.getBalance());
    }

    public UserCardDto toUserCardDto (Card card) {
        String encrypted = card.getCardNum();
        String decrypted = aesServiceImpl.decrypt(encrypted);
        String masked = maskingService.maskCardNumber(decrypted);
        return new UserCardDto(card.getId(),
                masked,
                card.getCardNum4(),
                card.getOwner(),
                card.getValidThru(),
                card.getCurrentStatus(),
                card.getRequestedStatus(),
                card.getBalance()
        );
    }
}
