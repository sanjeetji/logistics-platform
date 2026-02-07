package com.logistics.pricing.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for calculating distances and estimated times
 */
@Service
@Slf4j
public class DistanceService {

    private static final int EARTH_RADIUS_KM = 6371;
    private static final double AVERAGE_SPEED_KMH = 30.0;

    /**
     * Calculate distance between two points using Haversine formula
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = EARTH_RADIUS_KM * c;
        
        log.debug("Calculated distance: {} km between ({}, {}) and ({}, {})", 
                  distance, lat1, lon1, lat2, lon2);
        
        return distance;
    }

    /**
     * Estimate time to reach destination based on distance
     */
    public int estimateTime(double distanceKm) {
        double timeHours = distanceKm / AVERAGE_SPEED_KMH;
        int timeMinutes = (int) Math.ceil(timeHours * 60);
        
        log.debug("Estimated time: {} minutes for {} km", timeMinutes, distanceKm);
        
        return timeMinutes;
    }
}
