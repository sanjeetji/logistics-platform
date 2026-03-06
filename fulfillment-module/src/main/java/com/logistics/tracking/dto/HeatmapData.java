package com.logistics.tracking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeatmapData {
    private String geohash;
    private Long vehicleCount;
    private LocalDateTime windowStart;
    private LocalDateTime windowEnd;
}
