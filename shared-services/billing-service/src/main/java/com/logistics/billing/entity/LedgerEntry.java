package com.logistics.billing.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ledger_entries")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String clientId;

    @Column(nullable = false)
    private BigDecimal amount; // Positive for Credit, Negative for Debit

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntryType type;

    private String description;

    private String referenceId; // Invoice ID or Payment ID

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum EntryType {
        CREDIT, // Payment received, Credit Memo
        DEBIT   // Invoice issued, Refund
    }
}
