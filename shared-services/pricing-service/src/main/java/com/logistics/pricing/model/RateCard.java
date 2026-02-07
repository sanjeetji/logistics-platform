package com.logistics.pricing.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "rate_cards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RateCard extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "tenant_id")
    private String tenantId; // For B2B contracts

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PricingType type; // STANDARD, CONTRACT, SURGE

    @Column(name = "vehicle_type")
    private String vehicleType; // e.g., BIKE, TRUCK_3T

    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    @Column(name = "price_per_km", nullable = false)
    private BigDecimal pricePerKm;

    @Column(name = "price_per_minute", nullable = false)
    private BigDecimal pricePerMinute;

    @Column(name = "minimum_price")
    private BigDecimal minimumPrice;

    // createdAt and updatedAt are now handled by BaseEntity

    public enum PricingType {
        STANDARD, CONTRACT, SURGE
    }
}
