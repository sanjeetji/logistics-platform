package com.logistics.pricing.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "contract_terms")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractTerm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private EnterpriseContract contract;

    @Column(nullable = false, length = 50)
    private String vehicleType;

    @Column(length = 50)
    private String serviceLevel;

    @Column(precision = 10, scale = 2)
    private BigDecimal baseRate;

    @Column(precision = 10, scale = 2)
    private BigDecimal perKmRate;

    @Column(precision = 10, scale = 2)
    private BigDecimal perMinuteRate;

    @Column(precision = 5, scale = 2)
    private BigDecimal discountPercentage; // E.g., apply a flat 15% discount if specific rates aren't set
}
