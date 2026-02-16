package com.logistics.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Represents a single data point in a heatmap
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeatmapDataPoint {
    /**
     * Latitude coordinate
     */
    private Double latitude;

    /**
     * Longitude coordinate
     */
    private Double longitude;

    /**
     * Raw count (orders, drivers, etc.)
     */
    private Integer count;

    /**
     * Normalized intensity (0.0 to 1.0)
     */
    private Double intensity;

    /**
     * Additional metadata (avg price, peak time, etc.)
     */
    private Map<String, Object> metadata;
}
