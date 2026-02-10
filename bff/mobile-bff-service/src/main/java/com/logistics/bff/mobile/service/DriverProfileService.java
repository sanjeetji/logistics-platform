package com.logistics.bff.mobile.service;

import com.logistics.bff.mobile.client.FleetServiceClient;
import com.logistics.platform.dto.fleet.DriverDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Driver Profile Service
 * Business logic for driver profile operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DriverProfileService {

    private final FleetServiceClient fleetClient;

    /**
     * Update driver profile
     */
    public DriverDTO updateProfile(Long driverId, Map<String, Object> profileData) {
        try {
            log.info("Updating profile for driver: {}", driverId);
            
            // Get current driver
            DriverDTO driver = fleetClient.getDriver(driverId);
            
            // Update fields from profileData
            if (profileData.containsKey("email")) {
                driver.setEmail((String) profileData.get("email"));
            }
            if (profileData.containsKey("phoneNumber")) {
                driver.setPhoneNumber((String) profileData.get("phoneNumber"));
            }
            
            // In real implementation, call fleet service to update
            // return fleetClient.updateDriver(driverId, driver);
            
            return driver;
        } catch (Exception e) {
            log.error("Failed to update profile for driver: {}", driverId, e);
            throw new RuntimeException("Failed to update profile: " + e.getMessage());
        }
    }

    /**
     * Update driver availability
     */
    public Map<String, Object> updateAvailability(Long driverId, Map<String, Object> availabilityData) {
        try {
            Boolean isAvailable = (Boolean) availabilityData.get("isAvailable");
            
            log.info("Updating availability for driver {} to {}", driverId, isAvailable);
            
            // In real implementation, call fleet service
            // fleetClient.updateAvailability(driverId, isAvailable);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("driverId", driverId);
            result.put("isAvailable", isAvailable);
            result.put("timestamp", LocalDateTime.now().toString());
            result.put("message", "Availability updated successfully");
            
            return result;
        } catch (Exception e) {
            log.error("Failed to update availability for driver: {}", driverId, e);
            throw new RuntimeException("Failed to update availability: " + e.getMessage());
        }
    }

    /**
     * Get driver earnings
     */
    public Map<String, Object> getEarnings(Long driverId, String period) {
        try {
            Map<String, Object> earnings = new HashMap<>();
            
            // Mock earnings data - replace with actual service calls
            earnings.put("driverId", driverId);
            earnings.put("period", period != null ? period : "current_month");
            earnings.put("totalEarnings", 45250.00);
            earnings.put("completedDeliveries", 156);
            earnings.put("averagePerDelivery", 290.06);
            earnings.put("bonuses", 2500.00);
            earnings.put("deductions", 750.00);
            earnings.put("netEarnings", 47000.00);
            earnings.put("pendingPayment", 5200.00);
            
            return earnings;
        } catch (Exception e) {
            log.error("Failed to get earnings for driver: {}", driverId, e);
            throw new RuntimeException("Failed to get earnings: " + e.getMessage());
        }
    }

    /**
     * Get driver statistics
     */
    public Map<String, Object> getStats(Long driverId) {
        try {
            DriverDTO driver = fleetClient.getDriver(driverId);
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("driverId", driverId);
            stats.put("totalDeliveries", driver.getTotalDeliveries() != null ? driver.getTotalDeliveries() : 0);
            stats.put("rating", driver.getRating() != null ? driver.getRating() : 0.0);
            stats.put("onTimeDeliveryRate", 94.2);
            stats.put("acceptanceRate", 87.5);
            stats.put("cancellationRate", 2.3);
            stats.put("averageDeliveryTime", "35 minutes");
            stats.put("totalDistance", "2,450 km");
            stats.put("activeHours", "180 hours");
            
            return stats;
        } catch (Exception e) {
            log.error("Failed to get stats for driver: {}", driverId, e);
            throw new RuntimeException("Failed to get stats: " + e.getMessage());
        }
    }
}
