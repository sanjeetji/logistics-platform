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
public class RouteOptimizationRequest {
    private Location startLocation;
    private Location endLocation;
    private List<Location> waypoints; // Intermediate stops
    private String optimizationGoal; // SHORTEST_DISTANCE, SHORTEST_TIME, BALANCED
    private String vehicleType;
    private Boolean returnToStart; // For circular routes
}
