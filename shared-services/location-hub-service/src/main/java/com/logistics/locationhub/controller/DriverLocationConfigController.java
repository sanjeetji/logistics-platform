package com.logistics.locationhub.controller;

import com.logistics.locationhub.service.LocationOptimizationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/driver/config")
@RequiredArgsConstructor
public class DriverLocationConfigController {

    private final LocationOptimizationService optimizationService;

    @PostMapping
    public ResponseEntity<TrackingConfigResponse> getTrackingConfig(@RequestBody TrackingConfigRequest request) {
        int interval = optimizationService.calculateAdaptiveFrequency(request.getSpeed(), request.getBatteryLevel());
        return ResponseEntity.ok(new TrackingConfigResponse(interval));
    }

    @Data
    public static class TrackingConfigRequest {
        private Double speed;
        private Integer batteryLevel;
    }

    @Data
    @RequiredArgsConstructor
    public static class TrackingConfigResponse {
        private final int updateIntervalSeconds;
    }
}
