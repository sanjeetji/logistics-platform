package com.logistics.platform.common.dto.ml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandPredictionResponse {
    private String region;
    private LocalDate predictedDate;
    private int predictedDemand;
    private float confidence;
    private Map<String, Object> factors;
}
