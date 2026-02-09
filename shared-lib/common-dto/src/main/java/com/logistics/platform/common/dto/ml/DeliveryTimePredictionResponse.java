package com.logistics.platform.common.dto.ml;

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
    private int predictedTimeMinutes;
    private float confidence;
    private Map<String, Object> factors;
}
