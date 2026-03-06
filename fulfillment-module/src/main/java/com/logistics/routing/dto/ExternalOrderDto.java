package com.logistics.routing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalOrderDto {
    private String orderId;
    private OrderLocationDto pickupLocation;
    private OrderLocationDto dropLocation;
    private Double weightKg;
    private String timeSlot;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OrderLocationDto {
        private Double latitude;
        private Double longitude;
        private String address;
    }
}
