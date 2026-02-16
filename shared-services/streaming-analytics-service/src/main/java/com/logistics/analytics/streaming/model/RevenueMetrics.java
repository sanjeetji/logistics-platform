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
public class RevenueMetrics {
    
    private LocalDateTime timestamp;
    private String windowStart;
    private String windowEnd;
    
    // Revenue totals
    private double totalRevenue;
    private double grossRevenue;
    private double netRevenue;
    
    // Revenue by service type
    private double b2bRevenue;
    private double b2cRevenue;
    private double expressRevenue;
    private double standardRevenue;
    
    // Transaction metrics
    private long totalTransactions;
    private long successfulTransactions;
    private long failedTransactions;
    private double paymentSuccessRate;
    
    // Averages
    private double averageOrderValue;
    private double revenuePerHour;
    
    // Geographic breakdown
    private Map<String, Double> revenueByCity;
    
    // Payment methods
    private Map<String, Double> revenueByPaymentMethod;
}
