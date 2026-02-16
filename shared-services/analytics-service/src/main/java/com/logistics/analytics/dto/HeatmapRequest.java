package com.logistics.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request parameters for generating heatmaps
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeatmapRequest {
    /**
     * Geographic bounds for the heatmap
     */
    private BoundingBox bounds;

    /**
     * Grid cell size in degrees (default: 0.01 ≈ 1km)
     */
    @Builder.Default
    private Double gridSize = 0.01;

    /**
     * Time range for data aggregation (optional)
     */
    private TimeRange timeRange;

    /**
     * Minimum count threshold for inclusion (default: 1)
     */
    @Builder.Default
    private Integer minThreshold = 1;

    /**
     * Validate request parameters
     */
    public boolean isValid() {
        return bounds != null && bounds.isValid()
                && gridSize != null && gridSize > 0 && gridSize < 1
                && (timeRange == null || timeRange.isValid())
                && minThreshold != null && minThreshold >= 0;
    }
}
