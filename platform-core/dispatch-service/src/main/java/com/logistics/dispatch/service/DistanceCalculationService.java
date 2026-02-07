package com.logistics.dispatch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for calculating distances and routes
 */
@Service
@Slf4j
public class DistanceCalculationService {

    /**
     * Calculate distance between two points using Haversine formula
     * 
     * @param lat1 Latitude of point 1
     * @param lon1 Longitude of point 1
     * @param lat2 Latitude of point 2
     * @param lon2 Longitude of point 2
     * @return Distance in kilometers
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Estimate time to reach destination based on distance
     * Assumes average speed of 30 km/h in urban areas
     * 
     * @param distanceKm Distance in kilometers
     * @return Estimated time in minutes
     */
    public int estimateTimeToReach(double distanceKm) {
        final double AVERAGE_SPEED_KMH = 30.0;
        double timeHours = distanceKm / AVERAGE_SPEED_KMH;
        return (int) Math.ceil(timeHours * 60);
    }
}
