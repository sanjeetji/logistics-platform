package com.logistics.payout.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payouts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long driverId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayoutStatus status;

    private LocalDateTime generatedAt;

    private LocalDateTime approvedAt;
    private String approvedBy;

    private LocalDateTime paidAt;

    @PrePersist
    public void onCreate() {
        if (this.generatedAt == null) {
            this.generatedAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = PayoutStatus.PENDING;
        }
    }

    public enum PayoutStatus {
        PENDING,
        APPROVED,
        PAID,
        REJECTED,
        FAILED
    }
}
