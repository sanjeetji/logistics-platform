package com.logistics.analytics.controller;

import com.logistics.analytics.model.CarbonFootprint;
import com.logistics.analytics.service.EmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/analytics/emissions")
@RequiredArgsConstructor
public class AnalyticsController {

    private final EmissionService emissionService;

    @PostMapping("/calculate")
    public ResponseEntity<CarbonFootprint> calculateEmission(
            @RequestParam String entityId,
            @RequestParam String entityType,
            @RequestParam double distanceKm,
            @RequestParam String vehicleType) {
        return ResponseEntity
                .ok(emissionService.calculateAndSaveEmission(entityId, entityType, distanceKm, vehicleType));
    }

    @GetMapping("/total")
    public ResponseEntity<Double> getTotalEmissions() {
        return ResponseEntity.ok(emissionService.getTotalEmissions());
    }
}
