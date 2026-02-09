package com.logistics.platform.common.dto.ml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandPredictionRequest {
    private String region;
    private LocalDate date;
    private int historicalDays;
}
