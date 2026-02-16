package com.logistics.pricing.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "price_estimates")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PriceEstimate extends BaseEntity {

    @Column(unique = true)
    private String estimateId;

    private String orderId; // Optional, if estimate is for a specific order

    @Column(nullable = false)
    private String vehicleType;

    @Column(name = "service_level")
    private String serviceLevel;

    @Column(nullable = false)
    private Double distance; // in km

    private Integer estimatedTime; // in minutes

    // Price breakdown
    @Column(precision = 10, scale = 2)
    private BigDecimal baseFare;

    @Column(precision = 10, scale = 2)
    private BigDecimal distanceFare;

    @Column(precision = 10, scale = 2)
    private BigDecimal timeFare;

    @Column(precision = 10, scale = 2)
    private BigDecimal surgeFare;

    @Column(precision = 10, scale = 2)
    private BigDecimal serviceFee;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Builder.Default
    @Column(length = 10)
    private String currency = "INR";

    private java.time.LocalDateTime validUntil;

    @Column(columnDefinition = "text")
    private String notes;
}
