package com.logistics.platform.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WalletCreditedEvent extends BaseEvent {
    private String userId;
    private String walletId;
    private BigDecimal amount;
    private String transactionId;
    private String referenceId;
    private String type; // TOPUP, REFUND, etc.
}
