package com.logistics.analytics.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "carbon_footprints")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarbonFootprint extends BaseEntity {

    private String entityId; // e.g., Shipment ID, Parcel ID
    private String entityType;
    private double distanceKm;
    private String vehicleType;
    private double emissionFactor; // kg CO2 per km
    private double totalCo2EmissionKg;
    private LocalDateTime calculatedAt;
}
