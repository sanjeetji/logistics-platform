package com.logistics.platform.common.dto.ml;

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
    private LocationPoint startLocation;
    private List<LocationPoint> stops;
    private String vehicleType;
    private int maxStops;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationPoint {
        private double lat;
        private double lng;
        private String stopId;
    }
}
