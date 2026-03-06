package com.logistics.analytics.streaming.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverMetrics {
    
    private LocalDateTime timestamp;
    private String windowStart;
    private String windowEnd;
    
    // Driver counts by status
    private long totalDrivers;
    private long availableDrivers;
    private long busyDrivers;
    private long offlineDrivers;
    
    // Utilization metrics
    private double utilizationRate; // Busy / (Available + Busy)
    private double averageDeliveryTimeMinutes;
    private double averageDriverRating;
    
    // Geographic breakdown
    private Map<String, Long> driversByCity;
    private Map<String, Long> driversByZone;
    
    // Performance
    private long completedDeliveries;
    private long activeDeliveries;
    private double averageEarningsPerDriver;
}
