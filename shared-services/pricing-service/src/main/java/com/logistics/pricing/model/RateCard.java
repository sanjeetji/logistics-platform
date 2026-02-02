package com.logistics.pricing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rate_cards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateCard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum PricingType {
        STANDARD, CONTRACT, SURGE
    }
}
