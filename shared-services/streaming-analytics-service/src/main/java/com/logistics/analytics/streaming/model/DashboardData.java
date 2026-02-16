package com.logistics.analytics.streaming.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardData {
    private OrderMetrics orderMetrics;
    private DriverMetrics driverMetrics;
    private RevenueMetrics revenueMetrics;
    private SLAMetrics slaMetrics;
}
