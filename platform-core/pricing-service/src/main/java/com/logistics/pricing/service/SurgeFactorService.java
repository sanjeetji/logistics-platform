package com.logistics.pricing.service;

import com.logistics.pricing.model.SurgeZone;
import com.logistics.pricing.repository.SurgeZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurgeFactorService {

    private final SurgeZoneRepository surgeZoneRepository;
    private final DistanceService distanceService;

    private static final double PEAK_HOUR_MULTIPLIER = 1.2;
    // Morning Peak: 8 AM - 10 AM
    private static final LocalTime MORNING_PEAK_START = LocalTime.of(8, 0);
    private static final LocalTime MORNING_PEAK_END = LocalTime.of(10, 0);
    // Evening Peak: 5 PM - 8 PM
    private static final LocalTime EVENING_PEAK_START = LocalTime.of(17, 0);
    private static final LocalTime EVENING_PEAK_END = LocalTime.of(20, 0);

    public double getSurgeMultiplier(double latitude, double longitude) {
        double locationSurge = calculateLocationSurge(latitude, longitude);
        double timeSurge = calculateTimeSurge();

        // Return the maximum of location surge or time surge
        // Or could be multiplicative: locationSurge * timeSurge
        // For now, let's take the maximum to avoid excessive pricing
        double maxSurge = Math.max(locationSurge, timeSurge);

        if (maxSurge > 1.0) {
            log.info("Surge applied: {} (Location: {}, Time: {})", maxSurge, locationSurge, timeSurge);
        }

        return maxSurge;
    }

    private double calculateLocationSurge(double latitude, double longitude) {
        LocalDateTime now = LocalDateTime.now();
        List<SurgeZone> activeZones = surgeZoneRepository.findActiveSurgeZones(now);

        double maxSurge = 1.0;

        for (SurgeZone zone : activeZones) {
            double distance = distanceService.calculateDistance(
                    latitude, longitude,
                    zone.getLatitude(), zone.getLongitude());

            if (distance <= zone.getRadiusKm()) {
                log.debug("Location is in surge zone: {} with multiplier: {}",
                        zone.getZoneName(), zone.getSurgeMultiplier());
                maxSurge = Math.max(maxSurge, zone.getSurgeMultiplier());
            }
        }
        return maxSurge;
    }

    private double calculateTimeSurge() {
        LocalTime now = LocalTime.now();
        if ((now.isAfter(MORNING_PEAK_START) && now.isBefore(MORNING_PEAK_END)) ||
                (now.isAfter(EVENING_PEAK_START) && now.isBefore(EVENING_PEAK_END))) {
            return PEAK_HOUR_MULTIPLIER;
        }
        return 1.0;
    }
}
