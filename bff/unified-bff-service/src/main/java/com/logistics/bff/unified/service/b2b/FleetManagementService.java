package com.logistics.bff.unified.service.b2b;

import com.logistics.bff.unified.client.b2b.FleetServiceClient;
import com.logistics.platform.dto.fleet.DriverDTO;
import com.logistics.platform.dto.fleet.VehicleDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fleet Management Service
 * Business logic for fleet operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FleetManagementService {

    private final FleetServiceClient fleetClient;

    /**
     * Assign driver to an order
     */
    public Map<String, Object> assignDriver(Map<String, Object> assignmentData) {
        log.info("Assigning driver: {} to order: {}", assignmentData.get("driverId"), assignmentData.get("orderId"));
        try {
            fleetClient.assignDriver(assignmentData);
            return Map.of("success", true, "message", "Driver assigned successfully");
        } catch (Exception e) {
            log.error("Failed to assign driver", e);
            throw new RuntimeException("Driver assignment failed: " + e.getMessage());
        }
    }

    /**
     * Check driver availability
     */
    @Cacheable(value = "driver-availability", key = "#location + '-' + #timeSlot")
    public Map<String, Object> checkAvailability(String location, String timeSlot) {
        log.info("Checking availability at: {}, slot: {}", location, timeSlot);
        List<DriverDTO> drivers = fleetClient.getDrivers("AVAILABLE", true);

        Map<String, Object> availability = new HashMap<>();
        availability.put("availableCount", drivers.size());
        availability.put("drivers", drivers.stream()
                .limit(10)
                .map(d -> Map.of(
                        "id", d.getId(),
                        "name", d.getFirstName() + " " + d.getLastName(),
                        "rating", d.getRating() != null ? d.getRating() : 0.0,
                        "currentLocation", d.getCurrentLocation() != null ? d.getCurrentLocation() : "Unknown"))
                .toList());

        return availability;
    }

    /**
     * Get driver performance metrics
     */
    @Cacheable(value = "performance-metrics", key = "#driverId != null ? #driverId : 'all' + '-' + #period")
    public Map<String, Object> getPerformanceMetrics(Long driverId, String period) {
        log.info("Fetching performance metrics for driver: {}, period: {}", driverId, period);
        // Mock data
        return Map.of(
                "onTimeDeliveryRate", 0.95,
                "fuelEfficiency", "12 km/l",
                "safetyScore", 4.8,
                "totalOrdersCompleted", 150);
    }
}
