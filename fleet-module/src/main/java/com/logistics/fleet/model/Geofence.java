package com.logistics.fleet.model;

import jakarta.persistence.*;
import lombok.*;
import com.logistics.platform.event.dto.GeofencePurpose;
import org.locationtech.jts.geom.Geometry;

@Entity
@Table(name = "geofences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Geofence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private GeofenceType type; // CIRCLE, POLYGON, ZONE

    @Enumerated(EnumType.STRING)
    private GeofencePurpose purpose; // PICKUP, DELIVERY, etc.

    @Column(columnDefinition = "geometry(Geometry, 4326)")
    private Geometry boundary;

    // Metadata
    private String associatedEntityId; // orderId, warehouseId, etc.
    private String associatedEntityType; // ORDER, WAREHOUSE, SERVICE_AREA

    private Double radiusInMeters; // Only for Type=CIRCLE

    private boolean isActive = true;
}
