package com.logistics.masterdata.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vehicle_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // BIKE, CAR, VAN, TRUCK

    private String description;
    private Double maxCapacityKg;
    private Double maxVolumeCubicMeters;
    private Double baseRatePerKm;
    private boolean isActive;
}
