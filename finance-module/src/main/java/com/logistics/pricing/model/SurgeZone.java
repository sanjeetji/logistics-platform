package com.logistics.pricing.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "surge_zones")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SurgeZone extends BaseEntity {

    @Column(length = 100)
    private String zoneName;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Double radiusKm;

    @Builder.Default
    @Column(nullable = false)
    private Double surgeMultiplier = 1.0; // 1.0 = no surge, 2.0 = 2x price

    private LocalDateTime activeFrom;
    
    private LocalDateTime activeTo;

    @Column(length = 255)
    private String reason; // "High demand", "Peak hours", etc.

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;
}
