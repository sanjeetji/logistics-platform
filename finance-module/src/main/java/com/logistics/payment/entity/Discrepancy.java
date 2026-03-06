package com.logistics.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "discrepancies")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Discrepancy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long reconciliationId;

    private Long transactionId; // Local transaction ID if it exists

    private String gatewayReferenceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscrepancyType type;

    @Column(length = 1000)
    private String details;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum DiscrepancyType {
        AMOUNT_MISMATCH,
        STATUS_MISMATCH,
        MISSING_LOCAL,
        MISSING_GATEWAY
    }
}
