package com.logistics.payout.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payout_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayoutRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String payoutId;

    @Column(nullable = false)
    private String driverId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayoutStatus status;

    private String rejectionReason;
    
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;

    @PrePersist
    public void onCreate() {
        this.requestedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = PayoutStatus.PENDING;
        }
    }

    public enum PayoutStatus {
        PENDING,
        APPROVED,
        PROCESSED,
        REJECTED
    }
}
