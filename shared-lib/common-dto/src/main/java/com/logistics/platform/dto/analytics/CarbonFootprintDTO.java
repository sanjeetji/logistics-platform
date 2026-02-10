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
public class CarbonFootprintDTO {
    private String tenantId;
    private LocalDate date;
    private Double totalEmissions; // in kg CO2
    private Double emissionsPerDelivery; // in kg CO2
    private Double totalDistance; // in km
    private Integer totalDeliveries;
    private String vehicleType;
    private String period; // DAILY, WEEKLY, MONTHLY
}
