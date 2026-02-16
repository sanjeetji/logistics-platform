package com.logistics.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Defines a geographic bounding box for spatial queries
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoundingBox {
    /**
     * Minimum latitude (south)
     */
    private Double minLatitude;

    /**
     * Maximum latitude (north)
     */
    private Double maxLatitude;

    /**
     * Minimum longitude (west)
     */
    private Double minLongitude;

    /**
     * Maximum longitude (east)
     */
    private Double maxLongitude;

    /**
     * Validate bounding box coordinates
     */
    public boolean isValid() {
        return minLatitude != null && maxLatitude != null
                && minLongitude != null && maxLongitude != null
                && minLatitude < maxLatitude
                && minLongitude < maxLongitude
                && minLatitude >= -90 && maxLatitude <= 90
                && minLongitude >= -180 && maxLongitude <= 180;
    }
}
