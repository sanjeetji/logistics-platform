package com.logistics.route.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryTimePredictionResponse {
    private Integer predictedTimeMinutes;
    private Double confidence;
    private Map<String, Object> factors;
}
