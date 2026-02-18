package com.logistics.bff.unified.controller.mobile;

import com.logistics.bff.unified.client.FleetServiceClient;
import com.logistics.bff.unified.service.DriverProfileService;
import com.logistics.platform.dto.fleet.DriverDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Driver Profile Controller
 * Handles driver profile operations for mobile app
 */
@RestController
@RequestMapping("/api/v1/mobile/driver")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Driver Profile", description = "Driver profile management for mobile app")
public class DriverProfileController {

    private final FleetServiceClient fleetClient;
    private final DriverProfileService driverProfileService;

    @GetMapping("/profile")
    @Operation(summary = "Get profile", description = "Get driver profile information")
    public ResponseEntity<DriverDTO> getProfile(@RequestParam Long driverId) {
        log.info("Fetching driver profile: {}", driverId);
        return ResponseEntity.ok(fleetClient.getDriver(driverId));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update profile", description = "Update driver profile information")
    public ResponseEntity<DriverDTO> updateProfile(
            @RequestParam Long driverId,
            @RequestBody Map<String, Object> profileData) {
        log.info("Updating driver profile: {}", driverId);
        return ResponseEntity.ok(driverProfileService.updateProfile(driverId, profileData));
    }

    @PostMapping("/availability")
    @Operation(summary = "Update availability", description = "Update driver availability status")
    public ResponseEntity<Map<String, Object>> updateAvailability(
            @RequestParam Long driverId,
            @RequestBody Map<String, Object> availabilityData) {
        log.info("Updating driver availability: {}", driverId);
        return ResponseEntity.ok(driverProfileService.updateAvailability(driverId, availabilityData));
    }

    @GetMapping("/earnings")
    @Operation(summary = "Get earnings", description = "Get driver earnings summary")
    public ResponseEntity<Map<String, Object>> getEarnings(
            @RequestParam Long driverId,
            @RequestParam(required = false) String period) {
        log.info("Fetching driver earnings: {}, period: {}", driverId, period);
        return ResponseEntity.ok(driverProfileService.getEarnings(driverId, period));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get statistics", description = "Get driver statistics and performance")
    public ResponseEntity<Map<String, Object>> getStats(@RequestParam Long driverId) {
        log.info("Fetching driver stats: {}", driverId);
        return ResponseEntity.ok(driverProfileService.getStats(driverId));
    }
}
