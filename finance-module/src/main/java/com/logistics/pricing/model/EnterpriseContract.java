package com.logistics.pricing.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "enterprise_contracts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String clientId;

    @Column(nullable = false, length = 100)
    private String contractName;

    @Column(nullable = false)
    private LocalDateTime validFrom;

    @Column(nullable = false)
    private LocalDateTime validTo;

    @Column(nullable = false)
    private boolean isActive;

    @Column(precision = 10, scale = 2)
    private BigDecimal minimumMonthlyCommitment;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
