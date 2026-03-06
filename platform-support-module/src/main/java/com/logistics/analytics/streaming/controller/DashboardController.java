package com.logistics.analytics.streaming.controller;

import com.logistics.analytics.streaming.model.*;
import com.logistics.analytics.streaming.service.MetricsStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("analyticsDashboardController")
@RequestMapping("/api/analytics/realtime")
@RequiredArgsConstructor
@Tag(name = "Real-time Analytics", description = "Live metrics and analytics API")
public class DashboardController {

    private final MetricsStorageService metricsStorageService;

    @GetMapping("/orders")
    @Operation(summary = "Get current order metrics")
    public ResponseEntity<OrderMetrics> getOrderMetrics() {
        OrderMetrics metrics = metricsStorageService.getOrderMetrics();
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/drivers")
    @Operation(summary = "Get current driver metrics")
    public ResponseEntity<DriverMetrics> getDriverMetrics() {
        DriverMetrics metrics = metricsStorageService.getDriverMetrics();
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/revenue")
    @Operation(summary = "Get current revenue metrics")
    public ResponseEntity<RevenueMetrics> getRevenueMetrics() {
        RevenueMetrics metrics = metricsStorageService.getRevenueMetrics();
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/sla")
    @Operation(summary = "Get current SLA metrics")
    public ResponseEntity<SLAMetrics> getSLAMetrics() {
        SLAMetrics metrics = metricsStorageService.getSLAMetrics();
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get all metrics for dashboard")
    public ResponseEntity<DashboardData> getDashboard() {
        DashboardData dashboard = DashboardData.builder()
                .orderMetrics(metricsStorageService.getOrderMetrics())
                .driverMetrics(metricsStorageService.getDriverMetrics())
                .revenueMetrics(metricsStorageService.getRevenueMetrics())
                .slaMetrics(metricsStorageService.getSLAMetrics())
                .build();

        return ResponseEntity.ok(dashboard);
    }
}
