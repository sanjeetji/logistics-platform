package com.logistics.pricing.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@FeignClient(name = "ml-service-eta", url = "${ml.service.url}")
public interface MLEtaClient {

    @PostMapping("/api/v1/ml/predict/eta")
    ETAResponse predictEta(@RequestBody ETARequest request);

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class ETARequest {
        @JsonProperty("pickup_latitude")
        private Double pickupLatitude;

        @JsonProperty("pickup_longitude")
        private Double pickupLongitude;

        @JsonProperty("drop_latitude")
        private Double dropLatitude;

        @JsonProperty("drop_longitude")
        private Double dropLongitude;

        @JsonProperty("vehicle_type")
        private String vehicleType;

        @JsonProperty("traffic_condition")
        private String trafficCondition;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ETAResponse {
        @JsonProperty("distance_km")
        private Double distanceKm;

        @JsonProperty("estimated_minutes")
        private Integer estimatedMinutes;

        @JsonProperty("traffic_condition")
        private String trafficCondition;

        private Map<String, Object> details;
    }
}
