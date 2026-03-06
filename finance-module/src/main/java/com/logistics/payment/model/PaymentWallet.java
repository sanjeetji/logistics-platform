package com.logistics.payment.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payment_wallets")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentWallet extends BaseEntity {

    @Column(nullable = false, unique = true)
    private Long userId; // Customer or Driver

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletType walletType;

    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalCredits = BigDecimal.ZERO;

    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalDebits = BigDecimal.ZERO;

    @Builder.Default
    private Boolean active = true;

    public enum WalletType {
        CUSTOMER,
        DRIVER,
        MERCHANT
    }

    public void credit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
        this.totalCredits = this.totalCredits.add(amount);
    }

    public void debit(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        this.balance = this.balance.subtract(amount);
        this.totalDebits = this.totalDebits.add(amount);
    }
}
