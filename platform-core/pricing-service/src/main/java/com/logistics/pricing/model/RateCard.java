package com.logistics.pricing.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "rate_cards")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateCard extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String version; // e.g., "v1.0", "v2.0"

    private String description;

    @Column(nullable = false)
    private BigDecimal baseRate; // Base rate per km

    @Column(nullable = false)
    private BigDecimal perKmRate;

    private BigDecimal perMinuteRate;

    private BigDecimal minimumFare;

    // Distance-based pricing tiers: { "0-5": 10.0, "5-10": 8.0, "10+": 6.0 }
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, BigDecimal> distanceTiers;

    // Vehicle type multipliers: { "BIKE": 1.0, "CAR": 1.5, "TRUCK": 2.0 }
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, BigDecimal> vehicleTypeMultipliers;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDefault = false;
}
