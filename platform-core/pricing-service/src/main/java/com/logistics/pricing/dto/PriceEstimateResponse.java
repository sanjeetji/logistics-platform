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
    private Double distance;
    private Integer estimatedTime;
    
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
        private BigDecimal surgeFare;
        private BigDecimal serviceFee;
    }
}
