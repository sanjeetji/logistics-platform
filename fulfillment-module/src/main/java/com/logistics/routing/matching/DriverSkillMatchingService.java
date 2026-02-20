package com.logistics.routing.matching;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Driver Skill Matching Service
 * 
 * Matches drivers to deliveries based on skills and requirements
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DriverSkillMatchingService {

    /**
     * Find best driver for delivery requirements
     */
    public String findBestDriver(DeliveryRequirements requirements, List<DriverSkills> availableDrivers) {
        log.info("Finding best driver for stop: {}", requirements.getStopId());
        
        List<DriverMatch> matches = new ArrayList<>();
        
        for (DriverSkills driver : availableDrivers) {
            double score = calculateMatchScore(driver, requirements);
            if (score > 0) {
                matches.add(new DriverMatch(driver.getDriverId(), score));
            }
        }
        
        if (matches.isEmpty()) {
            log.warn("No suitable driver found for stop: {}", requirements.getStopId());
            return null;
        }
        
        // Sort by score descending
        matches.sort(Comparator.comparingDouble(DriverMatch::getScore).reversed());
        
        String bestDriverId = matches.get(0).getDriverId();
        log.info("Best driver found: {} with score: {}", bestDriverId, matches.get(0).getScore());
        
        return bestDriverId;
    }

    /**
     * Calculate match score between driver and requirements
     */
    private double calculateMatchScore(DriverSkills driver, DeliveryRequirements requirements) {
        double score = 100.0;
        
        // Required certifications (mandatory)
        if (requirements.getRequiredCertifications() != null && !requirements.getRequiredCertifications().isEmpty()) {
            if (driver.getCertifications() == null || 
                !driver.getCertifications().containsAll(requirements.getRequiredCertifications())) {
                return 0.0; // Disqualified
            }
        }
        
        // Required vehicle type (mandatory)
        if (requirements.getRequiredVehicleType() != null) {
            if (driver.getVehicleTypes() == null || 
                !driver.getVehicleTypes().contains(requirements.getRequiredVehicleType())) {
                return 0.0; // Disqualified
            }
        }
        
        // Minimum experience
        if (requirements.getMinimumExperience() != null) {
            if (driver.getYearsExperience() < requirements.getMinimumExperience()) {
                score -= 20.0;
            } else {
                score += Math.min(driver.getYearsExperience() * 2, 20.0);
            }
        }
        
        // Minimum rating
        if (requirements.getMinimumRating() != null) {
            if (driver.getAverageRating() < requirements.getMinimumRating()) {
                score -= 15.0;
            } else {
                score += (driver.getAverageRating() - requirements.getMinimumRating()) * 10;
            }
        }
        
        // Urgent delivery
        if (Boolean.TRUE.equals(requirements.getIsUrgent())) {
            if (Boolean.TRUE.equals(driver.getAvailableForUrgent())) {
                score += 15.0;
            } else {
                score -= 10.0;
            }
        }
        
        // Preferred zone
        if (requirements.getPreferredZone() != null && driver.getPreferredZones() != null) {
            if (driver.getPreferredZones().contains(requirements.getPreferredZone())) {
                score += 10.0;
            }
        }
        
        // Language match
        if (requirements.getRequiredLanguages() != null && !requirements.getRequiredLanguages().isEmpty()) {
            if (driver.getLanguages() != null) {
                long matchingLanguages = requirements.getRequiredLanguages().stream()
                    .filter(driver.getLanguages()::contains)
                    .count();
                score += matchingLanguages * 5.0;
            }
        }
        
        return Math.max(0, score);
    }

    /**
     * Match multiple deliveries to multiple drivers
     */
    public Map<String, String> matchDeliveriesToDrivers(
            List<DeliveryRequirements> deliveries, 
            List<DriverSkills> drivers) {
        
        log.info("Matching {} deliveries to {} drivers", deliveries.size(), drivers.size());
        
        Map<String, String> assignments = new HashMap<>();
        Set<String> assignedDrivers = new HashSet<>();
        
        // Sort deliveries by urgency
        List<DeliveryRequirements> sortedDeliveries = deliveries.stream()
            .sorted((d1, d2) -> Boolean.compare(
                Boolean.TRUE.equals(d2.getIsUrgent()),
                Boolean.TRUE.equals(d1.getIsUrgent())
            ))
            .collect(Collectors.toList());
        
        for (DeliveryRequirements delivery : sortedDeliveries) {
            // Filter out already assigned drivers
            List<DriverSkills> availableDrivers = drivers.stream()
                .filter(d -> !assignedDrivers.contains(d.getDriverId()))
                .collect(Collectors.toList());
            
            String bestDriver = findBestDriver(delivery, availableDrivers);
            if (bestDriver != null) {
                assignments.put(delivery.getStopId(), bestDriver);
                assignedDrivers.add(bestDriver);
            }
        }
        
        log.info("Matched {} deliveries to drivers", assignments.size());
        return assignments;
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    private static class DriverMatch {
        private String driverId;
        private double score;
    }
}
