package com.logistics.bff.unified.controller.mobile;

import com.logistics.bff.unified.service.DriverLocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Driver Location Controller
 * Handles location updates and navigation for driver mobile app
 */
@RestController
@RequestMapping("/api/v1/mobile/driver")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Driver Location", description = "Location management for driver mobile app")
public class DriverLocationController {

    private final DriverLocationService locationService;

    @PostMapping("/location/update")
    @Operation(summary = "Update location", description = "Update driver's current location")
    public ResponseEntity<Map<String, Object>> updateLocation(@RequestBody Map<String, Object> locationData) {
        log.info("Updating driver location");
        return ResponseEntity.ok(locationService.updateLocation(locationData));
    }

    @PostMapping("/location/batch")
    @Operation(summary = "Batch location update", description = "Update multiple location points in batch")
    public ResponseEntity<Map<String, Object>> batchUpdateLocation(
            @RequestBody List<Map<String, Object>> locationBatch) {
        log.info("Batch updating {} location points", locationBatch.size());
        return ResponseEntity.ok(locationService.batchUpdateLocation(locationBatch));
    }

    @GetMapping("/navigation/{orderId}")
    @Operation(summary = "Get navigation", description = "Get navigation details for an order")
    public ResponseEntity<Map<String, Object>> getNavigation(@PathVariable String orderId) {
        log.info("Fetching navigation for order: {}", orderId);
        return ResponseEntity.ok(locationService.getNavigation(orderId));
    }
}
