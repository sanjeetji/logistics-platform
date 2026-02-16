package com.logistics.pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceEstimateResponse {

    private String estimateId;
    private String vehicleType;
    private com.logistics.pricing.model.ServiceLevel serviceLevel;
    private com.logistics.pricing.dto.PriceEstimateRequest.DeliveryType deliveryType;
    private Double distance;
    private Integer estimatedTime;
    private Double surgeMultiplier;

    private PriceBreakdown breakdown;

    private BigDecimal totalPrice;
    private String currency;
    private LocalDateTime validUntil;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceBreakdown {
        private BigDecimal baseFare;
        private BigDecimal distanceFare;
        private BigDecimal timeFare;
        private BigDecimal weightFare;
        private BigDecimal surgeFare;
        private BigDecimal serviceFee;
    }
}
