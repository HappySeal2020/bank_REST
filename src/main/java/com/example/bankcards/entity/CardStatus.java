package com.example.bankcards.entity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Card statuses
 */
@Schema(description="Состояние банковской карты")
public enum  CardStatus {
    ACTIVE,
    BLOCKED,
    EXPIRED,
    BLOCK_REQUESTED
}
