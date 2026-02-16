package com.logistics.analytics.service;

import com.logistics.analytics.dto.*;
import com.logistics.analytics.repository.LocationAnalyticsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for generating demand heatmaps
 */
@Slf4j
@Service
public class DemandHeatmapService {

    private final LocationAnalyticsRepository repository;

    public DemandHeatmapService(LocationAnalyticsRepository repository) {
        this.repository = repository;
    }

    /**
     * Generate demand heatmap for given bounds and time range
     */
    @Cacheable(value = "demand_heatmap", key = "#request.bounds.toString() + '_' + #request.gridSize")
    public HeatmapResponse generateDemandHeatmap(HeatmapRequest request) {
        log.info("Generating demand heatmap for bounds: {}", request.getBounds());

        if (!request.isValid()) {
            throw new IllegalArgumentException("Invalid heatmap request parameters");
        }

        BoundingBox bounds = request.getBounds();
        TimeRange timeRange = request.getTimeRange();

        // Default to last 24 hours if no time range specified
        LocalDateTime startTime = timeRange != null ? timeRange.getStartTime() : LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = timeRange != null ? timeRange.getEndTime() : LocalDateTime.now();

        List<HeatmapDataPoint> dataPoints = repository.getDemandHeatmap(
                bounds.getMinLatitude(), bounds.getMaxLatitude(),
                bounds.getMinLongitude(), bounds.getMaxLongitude(),
                request.getGridSize(),
                startTime, endTime,
                request.getMinThreshold());

        // Normalize intensities
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
     * Identify high-demand hotspots
     */
    @Cacheable(value = "demand_hotspots", key = "#minOrders + '_' + #hours")
    public List<HeatmapDataPoint> identifyHotspots(int minOrders, int hours) {
        log.info("Identifying hotspots with minOrders={}, hours={}", minOrders, hours);

        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusHours(hours);

        List<HeatmapDataPoint> hotspots = repository.identifyHotspots(startTime, endTime, minOrders);
        normalizeIntensities(hotspots);

        return hotspots;
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
