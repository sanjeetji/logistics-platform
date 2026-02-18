package com.logistics.bff.unified.controller.b2b;

import com.logistics.bff.unified.service.b2b.FleetManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * B2B Fleet Management Controller
 */
@RestController
@RequestMapping("/api/v1/bff/b2b/fleet")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "B2B Fleet", description = "Fleet management for B2B clients")
public class B2BFleetController {

        private final FleetManagementService fleetService;

        @PostMapping("/assign")
        @Operation(summary = "Assign driver")
        public ResponseEntity<Map<String, Object>> assignDriver(@RequestBody Map<String, Object> assignmentData) {
                log.info("B2B driver assignment request received");
                return ResponseEntity.ok(fleetService.assignDriver(assignmentData));
        }

        @GetMapping("/availability")
        @Operation(summary = "Check availability")
        public ResponseEntity<Map<String, Object>> checkAvailability(
                        @RequestParam String location,
                        @RequestParam(required = false) String timeSlot) {
                log.info("B2B fleet availability check at: {}", location);
                return ResponseEntity.ok(fleetService.checkAvailability(location, timeSlot));
        }

        @GetMapping("/performance")
        @Operation(summary = "Driver performance")
        public ResponseEntity<Map<String, Object>> getPerformanceMetrics(
                        @RequestParam(required = false) Long driverId,
                        @RequestParam(required = false) String period) {
                log.info("B2B fleet performance metrics request");
                return ResponseEntity.ok(fleetService.getPerformanceMetrics(driverId, period));
        }
}
