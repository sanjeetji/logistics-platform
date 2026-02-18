package com.logistics.bff.unified.controller.mobile;

import com.logistics.bff.unified.service.mobile.DriverLocationService;
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
 */
@RestController
@RequestMapping("/api/v1/mobile/driver")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Driver Location", description = "Location management for driver mobile app")
public class DriverLocationController {

    private final DriverLocationService driverLocationService;

    @PostMapping("/location/update")
    @Operation(summary = "Update location")
    public ResponseEntity<Map<String, Object>> updateLocation(@RequestBody Map<String, Object> locationData) {
        log.info("Mobile driver location update request received");
        return ResponseEntity.ok(driverLocationService.updateLocation(locationData));
    }

    @PostMapping("/location/batch")
    @Operation(summary = "Batch location update")
    public ResponseEntity<Map<String, Object>> batchUpdateLocation(
            @RequestBody List<Map<String, Object>> locationBatch) {
        log.info("Mobile driver batch location update request received");
        return ResponseEntity.ok(driverLocationService.batchUpdateLocation(locationBatch));
    }

    @GetMapping("/navigation/{orderId}")
    @Operation(summary = "Get navigation")
    public ResponseEntity<Map<String, Object>> getNavigation(@PathVariable String orderId) {
        log.info("Mobile driver navigation request for order: {}", orderId);
        return ResponseEntity.ok(driverLocationService.getNavigation(orderId));
    }
}
