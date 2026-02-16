package com.logistics.routing.dto;

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

    // B2B: Optimize based on orders across vehicles (VRP)
    private List<String> orderIds; // Deprecated
    private List<Long> vehicleIds; // Deprecated

    private List<ShipmentDTO> shipments;
    private List<VehicleDTO> vehicles;

    @NotNull(message = "Route date is required")
    private LocalDate routeDate;

    private LocationDTO startLocation; // Depot/warehouse
    private LocationDTO endLocation; // Return location

    // Shared: Ad-hoc routing (TSP)
    private List<LocationDTO> waypoints;
    private Boolean returnToStart;

    private Map<String, Object> constraints; // Time windows, capacity, etc.

    @Builder.Default
    private OptimizationType optimizationType = OptimizationType.VRP_JSPRIT;

    public enum OptimizationType {
        VRP_JSPRIT, // Implementation: GraphHopper (B2B)
        TSP_NEAREST_NEIGHBOR // Implementation: Nearest Neighbor (Shared/Ad-hoc)
    }
}
