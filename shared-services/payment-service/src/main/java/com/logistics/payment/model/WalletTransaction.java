package com.logistics.payment.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransaction extends BaseEntity {

    @Column(nullable = false)
    private Long walletId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balanceBefore;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balanceAfter;

    private String referenceId; // Order ID, Payout ID, etc.

    @Enumerated(EnumType.STRING)
    private ReferenceType referenceType;

    private String description;

    @Column(nullable = false)
    private LocalDateTime transactionDate;

    public enum TransactionType {
        CREDIT,
        DEBIT
    }

    public enum ReferenceType {
        ORDER_PAYMENT,
        REFUND,
        PAYOUT,
        TOP_UP,
        WITHDRAWAL,
        ADJUSTMENT
    }
}
