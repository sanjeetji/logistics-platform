package com.logistics.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response containing heatmap data points
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeatmapResponse {
    /**
     * List of heatmap data points
     */
    private List<HeatmapDataPoint> dataPoints;

    /**
     * Total count across all points
     */
    private Long totalCount;

    /**
     * Maximum intensity value (for normalization reference)
     */
    private Double maxIntensity;

    /**
     * Grid size used for aggregation
     */
    private Double gridSize;
}
