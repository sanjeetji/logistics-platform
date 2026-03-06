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
@Table(name = "drivers", indexes = {
        @Index(name = "idx_driver_status", columnList = "status"),
        @Index(name = "idx_driver_current_order", columnList = "currentOrderId")
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Driver extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String licenseNumber;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DriverStatus status = DriverStatus.OFFLINE;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private org.locationtech.jts.geom.Point currentLocation;

    // Current assignment
    private String currentOrderId; // Current order assigned to this driver
    private Long currentVehicleId; // Current vehicle assigned to this driver

    private java.time.LocalDateTime lastLocationUpdate;

    @Column(columnDefinition = "text")
    private String notes;

    private Long fleetId;
    private String externalId; // ID in vendor's system
    private String vehicleType; // Primary vehicle type (e.g., VAN, TRUCK)

    @Builder.Default
    private Double performanceScore = 100.0; // 0-100

    private java.time.LocalDateTime shiftStartTime;
    private Integer totalDrivingMinutes;

    // createdAt and updatedAt are handled by BaseEntity
}
