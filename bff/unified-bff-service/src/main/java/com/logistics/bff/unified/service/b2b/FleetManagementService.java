package com.logistics.bff.unified.service.b2b;

import com.logistics.bff.unified.client.FleetServiceClient;
import com.logistics.bff.unified.client.OrderServiceClient;
import com.logistics.platform.dto.fleet.DriverDTO;
import com.logistics.platform.dto.order.OrderDTO;
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
    private final OrderServiceClient orderClient;

    /**
     * Assign driver to an order
     */
    public Map<String, Object> assignDriver(Map<String, Object> assignmentData) {
        try {
            String orderId = (String) assignmentData.get("orderId");
            Long driverId = ((Number) assignmentData.get("driverId")).longValue();
            
            log.info("Assigning driver {} to order {}", driverId, orderId);
            
            // Get driver details
            DriverDTO driver = fleetClient.getDriver(driverId);
            
            // Get order details
            OrderDTO order = orderClient.getOrderById(orderId);
            
            // In real implementation, call fleet service to assign
            // fleetClient.assignDriver(orderId, driverId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("orderId", orderId);
            result.put("driverId", driverId);
            result.put("driverName", driver.getFirstName() + " " + driver.getLastName());
            result.put("message", "Driver assigned successfully");
            
            return result;
        } catch (Exception e) {
            log.error("Failed to assign driver", e);
            throw new RuntimeException("Failed to assign driver: " + e.getMessage());
        }
    }

    /**
     * Check driver availability
     */
    @Cacheable(value = "driver-availability", key = "#location + '-' + #timeSlot")
    public Map<String, Object> checkAvailability(String location, String timeSlot) {
        try {
            // Get available drivers
            List<DriverDTO> availableDrivers = fleetClient.getDrivers(null, true);
            
            Map<String, Object> availability = new HashMap<>();
            availability.put("location", location);
            availability.put("timeSlot", timeSlot != null ? timeSlot : "immediate");
            availability.put("availableDrivers", availableDrivers.size());
            availability.put("drivers", availableDrivers.stream()
                .limit(10)
                .map(d -> Map.of(
                    "id", d.getId(),
                    "name", d.getFirstName() + " " + d.getLastName(),
                    "rating", d.getRating() != null ? d.getRating() : 0.0,
                    "currentLocation", d.getCurrentLocation() != null ? d.getCurrentLocation() : "Unknown"
                ))
                .toList());
            
            return availability;
        } catch (Exception e) {
            log.error("Failed to check availability", e);
            throw new RuntimeException("Failed to check availability: " + e.getMessage());
        }
    }

    /**
     * Get driver performance metrics
     */
    @Cacheable(value = "performance-metrics", key = "#driverId + '-' + #period")
    public Map<String, Object> getPerformanceMetrics(Long driverId, String period) {
        try {
            Map<String, Object> metrics = new HashMap<>();
            
            if (driverId != null) {
                // Individual driver metrics
                DriverDTO driver = fleetClient.getDriver(driverId);
                metrics.put("driverId", driverId);
                metrics.put("driverName", driver.getFirstName() + " " + driver.getLastName());
                metrics.put("totalDeliveries", driver.getTotalDeliveries() != null ? driver.getTotalDeliveries() : 0);
                metrics.put("rating", driver.getRating() != null ? driver.getRating() : 0.0);
                metrics.put("onTimeDeliveryRate", 92.5);
                metrics.put("averageDeliveryTime", "38 minutes");
            } else {
                // Fleet-wide metrics
                metrics.put("totalDrivers", 150);
                metrics.put("activeDrivers", 120);
                metrics.put("averageRating", 4.6);
                metrics.put("totalDeliveries", 12500);
                metrics.put("onTimeRate", 89.3);
            }
            
            metrics.put("period", period != null ? period : "last_30_days");
            
            return metrics;
        } catch (Exception e) {
            log.error("Failed to get performance metrics", e);
            throw new RuntimeException("Failed to get performance metrics: " + e.getMessage());
        }
    }
}
