package com.logistics.platform.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueMetricsDTO {
    private String tenantId;
    private LocalDate date;
    private BigDecimal totalRevenue;
    private BigDecimal grossRevenue;
    private BigDecimal netRevenue;
    private Integer totalOrders;
    private BigDecimal averageOrderValue;
    private String currency;
    private String period; // DAILY, WEEKLY, MONTHLY
}
