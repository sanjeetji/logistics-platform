package com.logistics.fleet.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
public class FleetForecastDTO {
    private String region;
    private LocalDate date;
    private Integer predictedDemand;
    private Long scheduledCapacity; // Total number of drivers/slots scheduled
    private Long capacityGap; // Capacity - Demand
    private String status; // SURPLUS, DEFICIT, BALANCED
    private String recommendation;
    private Map<String, Object> metadata;
}
