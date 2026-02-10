package com.logistics.bff.b2b.controller;

import com.logistics.bff.b2b.client.FleetServiceClient;
import com.logistics.bff.b2b.service.FleetManagementService;
import com.logistics.platform.dto.fleet.DriverDTO;
import com.logistics.platform.dto.fleet.VehicleDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * B2B Fleet Management Controller
 * Handles fleet operations for B2B clients
 */
@RestController
@RequestMapping("/api/v1/bff/b2b/fleet")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "B2B Fleet", description = "Fleet management for B2B clients")
public class B2BFleetController {

    private final FleetServiceClient fleetClient;
    private final FleetManagementService fleetManagementService;

    @GetMapping("/drivers")
    @Operation(summary = "List drivers", description = "Get all drivers with optional filters")
    public ResponseEntity<List<DriverDTO>> getDrivers(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean isAvailable) {
        log.info("Fetching drivers - status: {}, available: {}", status, isAvailable);
        return ResponseEntity.ok(fleetClient.getDrivers(status, isAvailable));
    }

    @GetMapping("/drivers/{id}")
    @Operation(summary = "Get driver details", description = "Get detailed information about a specific driver")
    public ResponseEntity<DriverDTO> getDriver(@PathVariable Long id) {
        log.info("Fetching driver: {}", id);
        return ResponseEntity.ok(fleetClient.getDriver(id));
    }

    @GetMapping("/vehicles")
    @Operation(summary = "List vehicles", description = "Get all vehicles with optional filters")
    public ResponseEntity<List<VehicleDTO>> getVehicles(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type) {
        log.info("Fetching vehicles - status: {}, type: {}", status, type);
        return ResponseEntity.ok(fleetClient.getVehicles(status, type));
    }

    @PostMapping("/assign")
    @Operation(summary = "Assign driver", description = "Assign a driver to an order")
    public ResponseEntity<Map<String, Object>> assignDriver(@RequestBody Map<String, Object> assignmentData) {
        log.info("Assigning driver to order");
        return ResponseEntity.ok(fleetManagementService.assignDriver(assignmentData));
    }

    @GetMapping("/availability")
    @Operation(summary = "Check availability", description = "Check driver availability for a specific time and location")
    public ResponseEntity<Map<String, Object>> checkAvailability(
            @RequestParam String location,
            @RequestParam(required = false) String timeSlot) {
        log.info("Checking driver availability for location: {}", location);
        return ResponseEntity.ok(fleetManagementService.checkAvailability(location, timeSlot));
    }

    @GetMapping("/performance")
    @Operation(summary = "Driver performance", description = "Get driver performance metrics")
    public ResponseEntity<Map<String, Object>> getPerformanceMetrics(
            @RequestParam(required = false) Long driverId,
            @RequestParam(required = false) String period) {
        log.info("Fetching performance metrics - driverId: {}, period: {}", driverId, period);
        return ResponseEntity.ok(fleetManagementService.getPerformanceMetrics(driverId, period));
    }
}
