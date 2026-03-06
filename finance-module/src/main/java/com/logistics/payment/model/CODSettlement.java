package com.logistics.payment.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cod_settlements")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CODSettlement extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String orderId;

    @Column(nullable = false)
    private String driverId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SettlementStatus status = SettlementStatus.PENDING_COLLECTION;

    private String hubId;
    private String bankReference;

    private LocalDateTime collectedAt;
    private LocalDateTime depositedAt;
    private LocalDateTime reconciledAt;

    public enum SettlementStatus {
        PENDING_COLLECTION,
        COLLECTED,
        DEPOSITED,
        RECONCILED
    }
}
