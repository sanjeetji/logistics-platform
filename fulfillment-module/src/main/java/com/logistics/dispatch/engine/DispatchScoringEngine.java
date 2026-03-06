package com.logistics.dispatch.engine;

import com.logistics.dispatch.dto.DriverScore;
import com.logistics.platform.common.dto.fleet.DriverLocationDto;
import com.logistics.platform.common.dto.order.TransportOrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DispatchScoringEngine {

    private final List<DispatchConstraint> constraints;
    private final List<ScoringRule> scoringRules;

    public List<DriverScore> scoreDrivers(TransportOrderDto order, List<DriverLocationDto> candidates) {
        log.info("Scoring {} candidates for order {}", candidates.size(), order.getOrderId());

        // 1. Filter candidates based on constraints
        List<DriverLocationDto> eligibleDrivers = candidates.stream()
                .filter(driver -> checkConstraints(order, driver))
                .collect(Collectors.toList());

        if (eligibleDrivers.isEmpty()) {
            log.warn("No eligible drivers found after constraints check");
            return Collections.emptyList();
        }

        // 2. Score eligible drivers
        List<DriverScore> scoredDrivers = new ArrayList<>();
        for (DriverLocationDto driver : eligibleDrivers) {
            double totalScore = 0.0;
            double maxPossibleScore = 0.0;

            for (ScoringRule rule : scoringRules) {
                double score = rule.calculateScore(order, driver);
                totalScore += score * rule.getWeight();
                maxPossibleScore += 100.0 * rule.getWeight(); // Assuming max score per rule is 100
            }

            // Normalize score to 0-100
            double outcomeScore = (maxPossibleScore > 0) ? (totalScore / maxPossibleScore) * 100.0 : 0.0;

            Long driverId = null;
            try {
                driverId = Long.parseLong(driver.getDriverId());
            } catch (NumberFormatException e) {
                log.warn("Invalid driver ID format: {}", driver.getDriverId());
                continue; // Skip invalid IDs
            }

            scoredDrivers.add(DriverScore.builder()
                    .driverId(driverId)
                    .score(outcomeScore)
                    .distanceToPickup(0.0) // needs calculation or separate population
                    .vehicleType(driver.getVehicleType()) // Assuming vehicleType is present
                    .build());
        }

        // Sort by score descending
        scoredDrivers.sort((d1, d2) -> Double.compare(d2.getScore(), d1.getScore()));

        return scoredDrivers;
    }

    private boolean checkConstraints(TransportOrderDto order, DriverLocationDto driver) {
        for (DispatchConstraint constraint : constraints) {
            if (!constraint.matches(order, driver)) {
                log.debug("Driver {} failed constraint: {}", driver.getDriverId(), constraint.reason());
                return false;
            }
        }
        return true;
    }
}
