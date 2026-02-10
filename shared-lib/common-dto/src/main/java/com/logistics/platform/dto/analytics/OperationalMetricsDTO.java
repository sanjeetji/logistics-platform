package com.logistics.platform.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationalMetricsDTO {
    private String tenantId;
    private LocalDate date;
    private Integer totalDeliveries;
    private Integer successfulDeliveries;
    private Integer failedDeliveries;
    private Double successRate;
    private Double averageDeliveryTime; // in minutes
    private Integer activeDrivers;
    private Integer activeVehicles;
    private Double vehicleUtilization; // percentage
    private String period; // DAILY, WEEKLY, MONTHLY
}
