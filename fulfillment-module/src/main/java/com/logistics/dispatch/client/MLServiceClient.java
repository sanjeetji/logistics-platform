package com.logistics.dispatch.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "ml-service", url = "${ml.service.url}")
public interface MLServiceClient {

    @PostMapping("/api/v1/ml/optimize/route")
    RouteOptimizationResponse optimizeRoute(@RequestBody RouteOptimizationRequest request);

    @PostMapping("/api/v1/ml/predict/driver-match")
    DriverMatchingResponse getDriverMatch(@RequestBody DriverMatchingRequest request);

    @Data
    @Builder
    class RouteOptimizationRequest {
        private Location depot;
        private List<OrderLocation> orders;
        private List<VehicleLocation> vehicles;
    }

    @Data
    class Location {
        private Double lat;
        private Double lon;
    }

    @Data
    class OrderLocation {
        private String id;
        private Double lat;
        private Double lon;
        private Double weight;
    }

    @Data
    class VehicleLocation {
        private String id;
        private Double lat;
        private Double lon;
        private Double capacity;
    }

    @Data
    class RouteOptimizationResponse {
        private String status;
        @JsonProperty("total_distance_meters")
        private Integer totalDistanceMeters;
        private List<VehicleRoute> routes;
    }

    @Data
    class VehicleRoute {
        @JsonProperty("vehicle_id")
        private String vehicleId;
        @JsonProperty("route_order_ids")
        private List<String> routeOrderIds;
        @JsonProperty("distance_meters")
        private Integer distanceMeters;
    }

    @Data
    @Builder
    class DriverMatchingRequest {
        @JsonProperty("order_id")
        private String orderId;

        @JsonProperty("pickup_lat")
        private Double pickupLat;

        @JsonProperty("pickup_lng")
        private Double pickupLng;

        @JsonProperty("required_vehicle")
        private String requiredVehicle;

        private List<DriverCandidate> candidates;
    }

    @Data
    @Builder
    class DriverCandidate {
        @JsonProperty("driver_id")
        private String driverId;

        @JsonProperty("current_lat")
        private Double currentLat;

        @JsonProperty("current_lng")
        private Double currentLng;

        @JsonProperty("vehicle_type")
        private String vehicleType;

        private Double rating;

        @JsonProperty("acceptance_rate")
        private Double acceptanceRate;
    }

    @Data
    class DriverMatchingResponse {
        @JsonProperty("order_id")
        private String orderId;

        @JsonProperty("ranked_drivers")
        private List<ScoredDriver> rankedDrivers;
    }

    @Data
    class ScoredDriver {
        @JsonProperty("driver_id")
        private String driverId;

        private Double score;

        @JsonProperty("distance_km")
        private Double distanceKm;

        private Map<String, Object> metadata;
    }
}
