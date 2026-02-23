package com.logistics.fleet.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vehicles")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Vehicle extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String licensePlate;

    @Column(nullable = false)
    private String conditions; // e.g., "Good", "Needs Maintainance"

    @Enumerated(EnumType.STRING)
    private VehicleType type;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VehicleStatus status = VehicleStatus.AVAILABLE;

    private Double capacityKg;
    private Double volumeCubicMeter;

    @Column(name = "active")
    @Builder.Default
    private boolean active = true;

    // Current assignment
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_driver_id")
    private Driver currentDriver;

    private String currentOrderId; // Current order assigned to this vehicle

    // Maintenance tracking
    private java.time.LocalDateTime lastMaintenanceDate;
    private Integer mileageKm;

    @Column(columnDefinition = "text")
    private String notes;
}
