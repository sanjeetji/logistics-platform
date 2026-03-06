package com.logistics.fleet.controller;

import com.logistics.fleet.model.Geofence;
import com.logistics.fleet.repository.GeofenceRepository;
import com.logistics.fleet.utils.GeoUtils;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Geometry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/geofences")
@RequiredArgsConstructor
public class GeofenceController {

    private final GeofenceRepository geofenceRepository;

    @PostMapping
    public ResponseEntity<Geofence> createGeofence(@RequestBody GeofenceRequest request) {
        Geometry boundary;
        if ("CIRCLE".equalsIgnoreCase(request.getType())) {
            // Buffer a point to create a circle
            boundary = GeoUtils.createPoint(request.getLatitude(), request.getLongitude())
                    .buffer(request.getRadiusInMeters() / 111320.0); // Rough approximation for degrees
        } else {
            // Future: support polygon WKT
            boundary = GeoUtils.createPoint(request.getLatitude(), request.getLongitude());
        }

        Geofence geofence = Geofence.builder()
                .name(request.getName())
                .type(request.getType().equalsIgnoreCase("CIRCLE") ? com.logistics.fleet.model.GeofenceType.CIRCLE
                        : com.logistics.fleet.model.GeofenceType.POLYGON)
                .boundary(boundary)
                .associatedEntityId(request.getAssociatedEntityId())
                .associatedEntityType(request.getAssociatedEntityType())
                .radiusInMeters(request.getRadiusInMeters())
                .build();

        return ResponseEntity.ok(geofenceRepository.save(geofence));
    }

    @GetMapping
    public ResponseEntity<List<Geofence>> getAllGeofences() {
        return ResponseEntity.ok(geofenceRepository.findAll());
    }
}

@lombok.Data
class GeofenceRequest {
    private String name;
    private String type; // CIRCLE, POLYGON
    private Double latitude;
    private Double longitude;
    private Double radiusInMeters;
    private String associatedEntityId;
    private String associatedEntityType;
}
