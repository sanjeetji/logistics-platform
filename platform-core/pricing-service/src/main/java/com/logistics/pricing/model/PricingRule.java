package com.logistics.pricing.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pricing_rules")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PricingRule extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String vehicleType; // BIKE, THREE_WHEELER, TATA_ACE, etc.

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private ServiceLevel serviceLevel = ServiceLevel.STANDARD;

    @Column(length = 50)
    private String fromZoneId; // Nullable (Global if null)

    @Column(length = 50)
    private String toZoneId; // Nullable (Global if null)

    @Builder.Default
    private Integer priority = 0; // Higher value = higher priority

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal baseFare;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal perKmRate;

    @Column(precision = 10, scale = 2)
    private BigDecimal perMinuteRate;

    @Column(precision = 10, scale = 2)
    private BigDecimal minimumFare;

    @Column(precision = 10, scale = 2)
    private BigDecimal maximumFare;

    @Column(nullable = false)
    @Builder.Default
    private Integer volumetricDivisor = 5000;

    @Column(precision = 10, scale = 2)
    private BigDecimal baseWeightKg; // Weight included in base fare

    @Column(precision = 10, scale = 2)
    private BigDecimal additionalWeightRate; // Price per kg over base limit

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    private LocalDateTime effectiveFrom;

    private LocalDateTime effectiveTo;

    @Column(columnDefinition = "text")
    private String description;
}
