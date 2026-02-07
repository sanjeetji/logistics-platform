package com.logistics.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardOverview {
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private Double slaComplianceRate;
    private Integer activeDrivers;
    private Double avgDeliveryTime;
    private Long todayOrders;
    private BigDecimal todayRevenue;
}
