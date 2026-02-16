package com.logistics.platform.event.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRouteDto {
    @JsonProperty("vehicle_id")
    private String vehicleId;
    @JsonProperty("route_order_ids")
    private List<String> routeOrderIds;
    @JsonProperty("distance_meters")
    private Integer distanceMeters;
}
