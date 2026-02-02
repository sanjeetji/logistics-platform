package com.logistics.fleet.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vehicles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String licensePlate;

    @Column(nullable = false)
    private String conditions; // e.g., "Good", "Needs Maintainance"

    @Enumerated(EnumType.STRING)
    private VehicleType type;

    private Double capacityKg;
    private Double volumeCubicMeter;
    
    @Column(name = "active")
    @Builder.Default
    private boolean active = true;

    // Link to driver (One-to-One or Many-to-One depending on model. 
    // Usually a vehicle is assigned to a driver for a shift, but for now simple association)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_driver_id")
    private Driver currentDriver;
}


