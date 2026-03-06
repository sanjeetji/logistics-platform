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
public class OrderMetrics {
    
    private LocalDateTime timestamp;
    private String windowStart;
    private String windowEnd;
    
    // Order counts by status
    private long totalOrders;
    private long createdOrders;
    private long assignedOrders;
    private long pickedUpOrders;
    private long inTransitOrders;
    private long deliveredOrders;
    private long cancelledOrders;
    private long failedOrders;
    
    // Order counts by type
    private long b2bOrders;
    private long b2cOrders;
    private long expressOrders;
    private long standardOrders;
    
    // Performance metrics
    private double averageProcessingTimeSeconds;
    private double successRate;
    private double failureRate;
    
    // Geographic breakdown
    private Map<String, Long> ordersByCity;
    private Map<String, Long> ordersByZone;
    
    // Additional stats
    private double ordersPerHour;
    private long peakOrders;
}
