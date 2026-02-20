package com.logistics.masterdata.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "service_zones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Long cityId;

    @Column(columnDefinition = "TEXT")
    private String polygonBoundary; // GeoJSON or WKT format

    private String pricingTier; // STANDARD, PREMIUM, ECONOMY
    private boolean isAvailable;
}
