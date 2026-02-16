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
public class SLAMetrics {
    
    private LocalDateTime timestamp;
    private String windowStart;
    private String windowEnd;
    
    // SLA compliance
    private long totalDeliveries;
    private long onTimeDeliveries;
    private long lateDeliveries;
    private double onTimePercentage;
    
    // Delay metrics
    private double averageDelayMinutes;
    private long maxDelayMinutes;
    private long minDelayMinutes;
    
    // SLA violations
    private long slaViolations;
    private long criticalViolations; // > 60 min delay
    private long minorViolations; // 15-60 min delay
    
    // By service tier
    private double expressOnTimeRate;
    private double standardOnTimeRate;
    private double b2bOnTimeRate;
    private double b2cOnTimeRate;
    
    // Penalty metrics
    private double estimatedPenalties;
    private long affectedCustomers;
}
