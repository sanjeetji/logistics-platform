package com.logistics.payment.entity;

import com.logistics.platform.common.dto.payment.PaymentDtos;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reconciliation_records")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentDtos.GatewayType gatewayType;

    @Column(nullable = false)
    private LocalDateTime rangeFrom;

    @Column(nullable = false)
    private LocalDateTime rangeTo;

    @CreationTimestamp
    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private Integer totalProcessed;

    private Integer totalDiscrepancies;

    @Enumerated(EnumType.STRING)
    private ReconciliationStatus status;

    public enum ReconciliationStatus {
        PENDING,
        COMPLETED,
        FAILED
    }
}
