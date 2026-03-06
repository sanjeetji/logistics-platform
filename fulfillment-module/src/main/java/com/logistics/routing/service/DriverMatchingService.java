package com.logistics.routing.service;

import com.logistics.order.model.Order;
import com.logistics.platform.common.dto.fleet.DriverDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Driver Matching Service
 * 
 * Ranks drivers based on proximity, performance, and fatigue.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DriverMatchingService {

    /**
     * Score a driver for a specific order
     * Higher score is better (0.0 - 100.0)
     */
    public double scoreDriver(DriverDto driver, Order order) {
        // 1. Distance Score (Weight: 40%)
        double distanceKm = calculateDistance(driver.getCurrentLatitude(), driver.getCurrentLongitude(),
                order.getPickupLocation().getLatitude(), order.getPickupLocation().getLongitude());
        double distanceScore = Math.max(0, 100 - (distanceKm * 5)); // 0 score at 20km

        // 2. Performance Score (Weight: 30%)
        double performanceScore = driver.getPerformanceScore() != null ? driver.getPerformanceScore() : 80.0;

        // 3. Fatigue Factor (Weight: 30%)
        double fatigueFactor = calculateFatigueFactor(driver);
        double fatigueScore = fatigueFactor * 100;

        // Weighted Average
        double finalScore = (distanceScore * 0.4) + (performanceScore * 0.3) + (fatigueScore * 0.3);

        log.debug("Scoring driver {}: DistanceScore={}, PerfScore={}, FatigueScore={}, Final={}",
                driver.getName(), distanceScore, performanceScore, fatigueScore, finalScore);

        return finalScore;
    }

    /**
     * Find best ranked drivers for an order
     */
    public List<DriverDto> findBestMatches(List<DriverDto> candidates, Order order) {
        return candidates.stream()
                .sorted(Comparator.comparingDouble((DriverDto d) -> scoreDriver(d, order)).reversed())
                .collect(Collectors.toList());
    }

    private double calculateFatigueFactor(DriverDto driver) {
        if (driver.getShiftStartTime() == null) {
            return 1.0;
        }

        long activeMinutes = Duration.between(driver.getShiftStartTime(), LocalDateTime.now()).toMinutes();

        // Linear decay between 4 and 12 hours
        if (activeMinutes < 240)
            return 1.0;
        if (activeMinutes > 720)
            return 0.0;

        return 1.0 - ((double) (activeMinutes - 240) / (720 - 240));
    }

    private double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null)
            return 50.0; // Penalize unknown locations

        double earthRadius = 6371; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }
}
