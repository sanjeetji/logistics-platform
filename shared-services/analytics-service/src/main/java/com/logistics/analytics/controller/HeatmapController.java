package com.logistics.analytics.controller;

import com.logistics.analytics.dto.*;
import com.logistics.analytics.service.DemandHeatmapService;
import com.logistics.analytics.service.DriverAvailabilityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for heatmap generation
 */
@Slf4j
@RestController
@RequestMapping("/api/analytics/heatmap")
public class HeatmapController {

    private final DemandHeatmapService demandService;
    private final DriverAvailabilityService driverService;

    public HeatmapController(DemandHeatmapService demandService,
            DriverAvailabilityService driverService) {
        this.demandService = demandService;
        this.driverService = driverService;
    }

    /**
     * Generate demand heatmap
     * 
     * @param request Heatmap request parameters
     * @return Heatmap data points
     */
    @PostMapping("/demand")
    public ResponseEntity<HeatmapResponse> getDemandHeatmap(@RequestBody HeatmapRequest request) {
        log.info("Received demand heatmap request: {}", request);
        HeatmapResponse response = demandService.generateDemandHeatmap(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Generate driver availability heatmap
     * 
     * @param request Heatmap request parameters
     * @return Heatmap data points
     */
    @PostMapping("/drivers")
    public ResponseEntity<HeatmapResponse> getDriverHeatmap(@RequestBody HeatmapRequest request) {
        log.info("Received driver availability heatmap request: {}", request);
        HeatmapResponse response = driverService.generateAvailabilityHeatmap(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Identify high-demand hotspots
     * 
     * @param minOrders Minimum orders for hotspot detection
     * @param hours     Time window in hours (default: 24)
     * @return List of hotspot locations
     */
    @GetMapping("/hotspots")
    public ResponseEntity<List<HeatmapDataPoint>> getHotspots(
            @RequestParam(defaultValue = "10") int minOrders,
            @RequestParam(defaultValue = "24") int hours) {
        log.info("Identifying hotspots with minOrders={}, hours={}", minOrders, hours);
        List<HeatmapDataPoint> hotspots = demandService.identifyHotspots(minOrders, hours);
        return ResponseEntity.ok(hotspots);
    }
}
