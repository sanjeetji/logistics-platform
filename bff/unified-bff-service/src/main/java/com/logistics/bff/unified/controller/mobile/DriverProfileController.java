package com.logistics.bff.unified.controller.mobile;

import com.logistics.bff.unified.client.mobile.FleetServiceClient;
import com.logistics.platform.dto.fleet.DriverDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Mobile Driver Profile Controller
 */
@RestController
@RequestMapping("/api/v1/mobile/driver")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Driver Profile", description = "Profile management for driver mobile app")
public class DriverProfileController {

        private final FleetServiceClient fleetClient;

        @GetMapping("/profile")
        @Operation(summary = "Get profile")
        public ResponseEntity<DriverDTO> getProfile(@RequestParam Long driverId) {
                log.info("Mobile driver profile request: {}", driverId);
                return ResponseEntity.ok(fleetClient.getDriverById(driverId));
        }

        @PutMapping("/profile")
        @Operation(summary = "Update profile")
        public ResponseEntity<DriverDTO> updateProfile(
                        @RequestParam Long driverId,
                        @RequestBody DriverDTO profileData) {
                log.info("Mobile driver profile update request: {}", driverId);
                return ResponseEntity.ok(fleetClient.updateDriver(driverId, profileData));
        }
}
