package com.logistics.pricing.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "serviceable_zones")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceableZone extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String zoneName;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Double radiusKm;

    @Column(nullable = false)
    private Boolean active;

    // Optional: List of allowed pincodes could be added here or as a separate
    // entity
    // For now, we use radius-based check
}
