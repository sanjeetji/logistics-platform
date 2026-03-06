package com.logistics.routing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Traffic Data DTO for distance matrix with traffic
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrafficData {

    private String originAddress;
    private String destinationAddress;
    private Double originLat;
    private Double originLon;
    private Double destinationLat;
    private Double destinationLon;
    
    private Long distanceMeters;
    private Long durationSeconds;
    private Long durationInTrafficSeconds;
    
    private TrafficLevel trafficLevel;
    private Double trafficDelayPercent;
    
    private Long timestamp;
    private String source; // "GOOGLE_MAPS", "CACHE", "FALLBACK"

    public enum TrafficLevel {
        LIGHT,      // < 10% delay
        MODERATE,   // 10-25% delay
        HEAVY,      // 25-50% delay
        SEVERE      // > 50% delay
    }

    /**
     * Calculate traffic delay percentage
     */
    public Double calculateTrafficDelay() {
        if (durationSeconds == null || durationInTrafficSeconds == null || durationSeconds == 0) {
            return 0.0;
        }
        return ((durationInTrafficSeconds - durationSeconds) * 100.0) / durationSeconds;
    }

    /**
     * Determine traffic level from delay
     */
    public TrafficLevel determineTrafficLevel() {
        double delay = calculateTrafficDelay();
        if (delay < 10) return TrafficLevel.LIGHT;
        if (delay < 25) return TrafficLevel.MODERATE;
        if (delay < 50) return TrafficLevel.HEAVY;
        return TrafficLevel.SEVERE;
    }
}
