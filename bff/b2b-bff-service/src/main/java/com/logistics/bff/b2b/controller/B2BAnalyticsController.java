package com.logistics.bff.b2b.controller;

import com.logistics.bff.b2b.service.AnalyticsAggregationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * B2B Analytics Controller
 * Handles analytics and reporting for B2B clients
 */
@RestController
@RequestMapping("/api/v1/bff/b2b/analytics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "B2B Analytics", description = "Analytics and reporting for B2B clients")
public class B2BAnalyticsController {

    private final AnalyticsAggregationService analyticsService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard", description = "Get comprehensive analytics dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(
            @RequestParam(required = false) String period) {
        log.info("Fetching analytics dashboard for period: {}", period);
        return ResponseEntity.ok(analyticsService.getDashboard(period));
    }

    @GetMapping("/revenue")
    @Operation(summary = "Get revenue analytics", description = "Get detailed revenue analytics and trends")
    public ResponseEntity<Map<String, Object>> getRevenueAnalytics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        log.info("Fetching revenue analytics from {} to {}", startDate, endDate);
        return ResponseEntity.ok(analyticsService.getRevenueAnalytics(startDate, endDate));
    }

    @GetMapping("/performance")
    @Operation(summary = "Get performance metrics", description = "Get operational performance metrics")
    public ResponseEntity<Map<String, Object>> getPerformanceMetrics(
            @RequestParam(required = false) String metric) {
        log.info("Fetching performance metrics: {}", metric);
        return ResponseEntity.ok(analyticsService.getPerformanceMetrics(metric));
    }

    @GetMapping("/trends")
    @Operation(summary = "Get trend analysis", description = "Get trend analysis and predictions")
    public ResponseEntity<Map<String, Object>> getTrendAnalysis(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer days) {
        log.info("Fetching trend analysis for category: {}, days: {}", category, days);
        return ResponseEntity.ok(analyticsService.getTrendAnalysis(category, days));
    }
}
