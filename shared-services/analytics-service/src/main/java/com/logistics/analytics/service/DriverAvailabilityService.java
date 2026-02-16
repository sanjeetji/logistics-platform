package com.logistics.analytics.service;

import com.logistics.analytics.dto.*;
import com.logistics.analytics.repository.LocationAnalyticsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for driver availability analytics
 */
@Slf4j
@Service
public class DriverAvailabilityService {

    private final LocationAnalyticsRepository repository;

    public DriverAvailabilityService(LocationAnalyticsRepository repository) {
        this.repository = repository;
    }

    /**
     * Generate driver availability heatmap
     */
    @Cacheable(value = "driver_availability", key = "#request.bounds.toString() + '_' + #request.gridSize")
    public HeatmapResponse generateAvailabilityHeatmap(HeatmapRequest request) {
        log.info("Generating driver availability heatmap for bounds: {}", request.getBounds());

        if (!request.isValid()) {
            throw new IllegalArgumentException("Invalid heatmap request parameters");
        }

        BoundingBox bounds = request.getBounds();

        List<HeatmapDataPoint> dataPoints = repository.getDriverAvailabilityHeatmap(
                bounds.getMinLatitude(), bounds.getMaxLatitude(),
                bounds.getMinLongitude(), bounds.getMaxLongitude(),
                request.getGridSize());

        // Normalize intensities based on driver count
        normalizeIntensities(dataPoints);

        long totalCount = dataPoints.stream()
                .mapToLong(HeatmapDataPoint::getCount)
                .sum();

        double maxIntensity = dataPoints.stream()
                .mapToDouble(HeatmapDataPoint::getIntensity)
                .max()
                .orElse(1.0);

        return HeatmapResponse.builder()
                .dataPoints(dataPoints)
                .totalCount(totalCount)
                .maxIntensity(maxIntensity)
                .gridSize(request.getGridSize())
                .build();
    }

    /**
     * Normalize intensity values to 0-1 range
     */
    private void normalizeIntensities(List<HeatmapDataPoint> dataPoints) {
        if (dataPoints.isEmpty()) {
            return;
        }

        int maxCount = dataPoints.stream()
                .mapToInt(HeatmapDataPoint::getCount)
                .max()
                .orElse(1);

        dataPoints.forEach(point -> {
            double intensity = (double) point.getCount() / maxCount;
            point.setIntensity(intensity);
        });
    }
}
