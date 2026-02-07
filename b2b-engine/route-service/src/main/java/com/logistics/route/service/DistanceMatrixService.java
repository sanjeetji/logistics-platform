package com.logistics.route.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Service for distance and time calculations with caching
 */
@Service
@Slf4j
public class DistanceMatrixService {

    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Calculate distance between two points using Haversine formula
     * Results are cached in Redis
     */
    @Cacheable(value = "distances", key = "#lat1 + '-' + #lon1 + '-' + #lat2 + '-' + #lon2")
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Calculate estimated travel time (assumes 30 km/h average speed)
     */
    public int calculateTravelTime(double distanceKm) {
        return (int) ((distanceKm / 30.0) * 60); // minutes
    }

    /**
     * Build distance matrix for multiple locations
     */
    public double[][] buildDistanceMatrix(double[][] coordinates) {
        int n = coordinates.length;
        double[][] matrix = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    matrix[i][j] = 0;
                } else {
                    matrix[i][j] = calculateDistance(
                            coordinates[i][0], coordinates[i][1],
                            coordinates[j][0], coordinates[j][1]
                    );
                }
            }
        }

        return matrix;
    }
}
