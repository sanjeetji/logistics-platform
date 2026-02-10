package com.logistics.routing.dto;

import com.logistics.routing.model.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteOptimizationResponse {
    private List<Location> optimizedRoute;
    private Double totalDistanceKm;
    private Integer estimatedTimeMinutes;
    private String algorithm; // TSP, NEAREST_NEIGHBOR, GENETIC
    private Double savingsPercentage; // Compared to sequential route
}
