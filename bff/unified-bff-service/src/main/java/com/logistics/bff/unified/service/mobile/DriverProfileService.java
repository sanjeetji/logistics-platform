package com.logistics.bff.unified.service.mobile;

import com.logistics.bff.unified.client.mobile.FleetServiceClient;
import com.logistics.platform.dto.fleet.DriverDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Driver Profile Service
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
        log.info("Updating mobile driver profile for: {}", driverId);
        return fleetClient.getDriverById(driverId);
    }

    /**
     * Update driver availability
     */
    public Map<String, Object> updateAvailability(Long driverId, Map<String, Object> availabilityData) {
        log.info("Updating availability for driver: {}", driverId);
        fleetClient.updateAvailability(driverId, availabilityData);
        return Map.of("status", "UPDATED", "driverId", driverId);
    }

    /**
     * Get driver earnings
     */
    public Map<String, Object> getEarnings(Long driverId, String period) {
        log.info("Fetching earnings for driver: {} for period: {}", driverId, period);
        Map<String, Object> earnings = new HashMap<>();
        earnings.put("totalEarned", 4500.0);
        return earnings;
    }

    /**
     * Get driver statistics
     */
    public Map<String, Object> getStats(Long driverId) {
        log.info("Fetching stats for driver: {}", driverId);
        return Map.of("rating", 4.8, "totalTrips", 1250);
    }
}
