package com.logistics.pricing.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "surge_pricing_rules")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurgePricingRule extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SurgeType surgeType; // TIME_BASED, DEMAND_BASED, WEATHER_BASED

    // Time-based surge
    private LocalTime startTime;
    private LocalTime endTime;

    // Demand-based surge
    private Integer demandThreshold; // Number of pending orders to trigger surge

    // Multiplier
    @Column(nullable = false)
    private BigDecimal multiplier; // e.g., 1.5 for 50% increase

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    private Integer priority; // Higher priority rules apply first

    public enum SurgeType {
        TIME_BASED,      // Peak hours (morning/evening rush)
        DEMAND_BASED,    // High order volume
        WEATHER_BASED,   // Rain, snow (future)
        EVENT_BASED      // Concerts, sports events (future)
    }
}
