package com.logistics.locationhub.service;

import com.logistics.locationhub.dto.LocationUpdateDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationOptimizationService {

    // Thresholds
    private static final double HIGH_SPEED_THRESHOLD = 30.0; // km/h
    private static final double MEDIUM_SPEED_THRESHOLD = 10.0; // km/h
    private static final int LOW_BATTERY_THRESHOLD = 15; // Percentage

    // Frequencies (in seconds)
    private static final int FREQ_HIGH_SPEED = 5;
    private static final int FREQ_MEDIUM_SPEED = 10;
    private static final int FREQ_STATIONARY = 60;

    /**
     * Calculates the adaptive tracking frequency based on speed and battery level.
     * 
     * @param speed        Speed in km/h
     * @param batteryLevel Battery level percentage (0-100)
     * @return Recommended update interval in seconds
     */
    public int calculateAdaptiveFrequency(Double speed, Integer batteryLevel) {
        int interval = FREQ_STATIONARY;

        if (speed != null) {
            if (speed > HIGH_SPEED_THRESHOLD) {
                interval = FREQ_HIGH_SPEED;
            } else if (speed > MEDIUM_SPEED_THRESHOLD) {
                interval = FREQ_MEDIUM_SPEED;
            }
        }

        // If battery is low, double the interval to save power
        if (batteryLevel != null && batteryLevel < LOW_BATTERY_THRESHOLD) {
            interval = interval * 2;
        }

        return interval;
    }

    /**
     * Applies a simple Kalman Filter to smooth location data.
     * This is a simplified 1D implementation for demonstration.
     * In a real world, you'd use a 2D/3D filter with matrix operations.
     */
    public LocationUpdateDTO smoothLocation(LocationUpdateDTO raw, LocationUpdateDTO previous) {
        if (previous == null) {
            return raw;
        }

        // Simple weighted average based on accuracy
        double accuracyTotal = (raw.getAccuracy() != null ? raw.getAccuracy() : 10) +
                (previous.getAccuracy() != null ? previous.getAccuracy() : 10);

        double rawWeight = (previous.getAccuracy() != null ? previous.getAccuracy() : 10) / accuracyTotal;
        double prevWeight = (raw.getAccuracy() != null ? raw.getAccuracy() : 10) / accuracyTotal;

        double smoothedLat = (raw.getLatitude() * rawWeight) + (previous.getLatitude() * prevWeight);
        double smoothedLon = (raw.getLongitude() * rawWeight) + (previous.getLongitude() * prevWeight);

        return LocationUpdateDTO.builder()
                .driverId(raw.getDriverId())
                .latitude(smoothedLat)
                .longitude(smoothedLon)
                .accuracy(raw.getAccuracy()) // Keep raw accuracy or recalculate
                .speed(raw.getSpeed())
                .timestamp(raw.getTimestamp())
                .build();
    }

    /**
     * Compresses a list of location points using encoded polyline algorithm.
     * (Simplified simulation as actual algorithm is complex string manipulation)
     * 
     * @param trajectory List of location points
     * @return Encoded string
     */
    public String compressTrajectory(List<LocationUpdateDTO> trajectory) {
        // In a real implementation, use Google's Polyline Encoding Algorithm
        // For now, we return a simple JSON-like string representation
        StringBuilder sb = new StringBuilder();
        for (LocationUpdateDTO point : trajectory) {
            sb.append(String.format("%.5f,%.5f;", point.getLatitude(), point.getLongitude()));
        }
        return sb.toString();
    }
}
