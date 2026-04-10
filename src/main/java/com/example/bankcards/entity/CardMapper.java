package com.example.bankcards.entity;

import com.example.bankcards.dto.AdminCardDto;
import com.example.bankcards.dto.UserCardDto;
import com.example.bankcards.service.AesService;
import com.example.bankcards.service.MaskingService;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {
    private final MaskingService maskingService;
    private final AesService aesService;

    public CardMapper(MaskingService maskingService, AesService aesService) {
        this.maskingService = maskingService;
        this.aesService = aesService;
    }
    public AdminCardDto toDto(Card card) {
        String encrypted = card.getCardNum();
        String decrypted = aesService.decrypt(encrypted);
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
        String decrypted = aesService.decrypt(encrypted);
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
