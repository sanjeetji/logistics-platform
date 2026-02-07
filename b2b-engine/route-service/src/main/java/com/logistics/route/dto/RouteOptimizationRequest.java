package com.logistics.route.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteOptimizationRequest {
    
    @NotEmpty(message = "Order IDs are required")
    private List<String> orderIds;
    
    @NotEmpty(message = "Vehicle IDs are required")
    private List<Long> vehicleIds;
    
    @NotNull(message = "Route date is required")
    private LocalDate routeDate;
    
    private LocationDTO startLocation; // Depot/warehouse
    
    private LocationDTO endLocation; // Return location
    
    private Map<String, Object> constraints; // Time windows, capacity, etc.
}
