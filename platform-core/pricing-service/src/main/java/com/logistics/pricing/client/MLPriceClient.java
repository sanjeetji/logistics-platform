package com.logistics.pricing.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "ml-service-pricing", url = "${ml.service.url}")
public interface MLPriceClient {

    @PostMapping("/api/v1/ml/pricing/calculate")
    PriceResponse calculatePrice(@RequestBody PriceRequest request);

    @Data
    @Builder
    class PriceRequest {
        private String region;

        @JsonProperty("distance_km")
        private Double distanceKm;

        @JsonProperty("vehicle_type")
        private String vehicleType;

        @JsonProperty("time_of_day")
        private String timeOfDay;

        @JsonProperty("current_demand")
        private Integer currentDemand;
    }

    @Data
    class PriceResponse {
        @JsonProperty("base_price")
        private Double basePrice;

        @JsonProperty("surge_multiplier")
        private Double surgeMultiplier;

        @JsonProperty("final_price")
        private Double finalPrice;

        private Map<String, Object> factors;
    }
}
