package com.logistics.analytics.streaming.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyAlert {
    
    private String alertId;
    private String alertType; // ORDER_SPIKE, DRIVER_DROP, REVENUE_DROP, SLA_VIOLATION, ERROR_SPIKE
    private String severity; // CRITICAL, WARNING, INFO
    private LocalDateTime timestamp;
    
    // Anomaly details
    private String metricName;
    private double currentValue;
    private double expectedValue;
    private double deviation;
    private double threshold;
    
    // Context
    private String description;
    private String recommendation;
    
    // Status
    private boolean acknowledged;
    private LocalDateTime acknowledgedAt;
    private String acknowledgedBy;
}
