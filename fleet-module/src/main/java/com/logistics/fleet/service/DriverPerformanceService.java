package com.logistics.fleet.service;

import com.logistics.fleet.model.Driver;
import com.logistics.fleet.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Driver Performance Service
 * 
 * Handles fatigue calculation and performance scoring logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DriverPerformanceService {

    private final DriverRepository driverRepository;

    /**
     * Calculate fatigue factor (0.0 to 1.0)
     * 1.0 = Fresh, 0.0 = Extremely fatigued
     */
    public double getFatigueFactor(Driver driver) {
        if (driver.getShiftStartTime() == null) {
            return 1.0;
        }

        long activeMinutes = Duration.between(driver.getShiftStartTime(), LocalDateTime.now()).toMinutes();

        // Safety limit: 12 hours (720 minutes)
        if (activeMinutes >= 720) {
            return 0.0;
        }

        // Linear decay after 4 hours
        if (activeMinutes > 240) {
            return 1.0 - ((double) (activeMinutes - 240) / (720 - 240));
        }

        return 1.0;
    }

    /**
     * Update driver performance score based on activity result
     */
    @Transactional
    public void updatePerformanceScore(String driverId, boolean positiveEvent) {
        driverRepository.findAll().stream() // Simplified, should use findByExternalId or similar
                .filter(d -> driverId.equals(d.getExternalId()))
                .findFirst()
                .ifPresent(driver -> {
                    double currentScore = driver.getPerformanceScore() != null ? driver.getPerformanceScore() : 100.0;
                    double adjustment = positiveEvent ? 1.0 : -5.0;
                    driver.setPerformanceScore(Math.max(0.0, Math.min(100.0, currentScore + adjustment)));
                    driverRepository.save(driver);
                    log.info("Updated driver {} performance score to {}", driverId, driver.getPerformanceScore());
                });
    }
}
