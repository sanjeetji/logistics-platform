package com.logistics.fleet.service;

import com.logistics.fleet.dto.DriverBehaviorEventDto;
import com.logistics.fleet.model.Driver;
import com.logistics.fleet.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverBehaviorService {

    private final DriverRepository driverRepository;
    private final DriverPerformanceService driverPerformanceService;

    /**
     * Process a new behavior event (e.g., from telematics) and update the driver's
     * score.
     */
    @Transactional
    public void processBehaviorEvent(DriverBehaviorEventDto eventDto) {
        log.info("Processing behavior event {} for driver {}", eventDto.getEventType(), eventDto.getDriverExternalId());

        driverRepository.findAll().stream() // In real app, findByExternalId
                .filter(d -> eventDto.getDriverExternalId().equals(d.getExternalId()))
                .findFirst()
                .ifPresent(driver -> {
                    double penalty = calculatePenalty(eventDto);

                    if (penalty > 0) {
                        applyPenaltyToPerformanceScore(driver, penalty);
                    }
                });
    }

    private double calculatePenalty(DriverBehaviorEventDto eventDto) {
        double basePenalty = switch (eventDto.getEventType()) {
            case SPEEDING -> 5.0;
            case HARD_BRAKING -> 3.0;
            case HARSH_ACCELERATION -> 2.0;
            case PHONE_USAGE -> 10.0;
            case LONG_IDLING -> 1.0;
        };

        // Apply severity multiplier if provided (0.0 to 1.0)
        double severityMultiplier = eventDto.getSeverity() != null ? eventDto.getSeverity() : 1.0;
        return basePenalty * severityMultiplier;
    }

    private void applyPenaltyToPerformanceScore(Driver driver, double penalty) {
        // Delegate to centrally managed performance service if applicable or handle
        // locally
        double currentScore = driver.getPerformanceScore() != null ? driver.getPerformanceScore() : 100.0;
        double newScore = Math.max(0.0, currentScore - penalty);
        driver.setPerformanceScore(newScore);
        driverRepository.save(driver);

        log.warn("Applied penalty of {} to driver {} for behavior event. New score: {}",
                penalty, driver.getExternalId(), newScore);

        // Ensure other systems tracking performance know about this negative event
        driverPerformanceService.updatePerformanceScore(driver.getExternalId(), false); // Trigger any side effects
    }
}
