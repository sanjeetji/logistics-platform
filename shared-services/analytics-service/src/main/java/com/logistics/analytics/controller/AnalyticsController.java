package com.logistics.analytics.controller;

import com.logistics.analytics.dto.DashboardOverview;
import com.logistics.analytics.dto.EventMessage;
import com.logistics.analytics.model.*;
import com.logistics.analytics.service.EventIngestionService;
import com.logistics.analytics.service.MetricsAggregationService;
import com.logistics.analytics.service.RevenueAnalyticsService;
import com.logistics.platform.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final EventIngestionService eventIngestionService;
    private final MetricsAggregationService metricsService;
    private final RevenueAnalyticsService revenueService;

    @PostMapping("/events")
    public ResponseEntity<ApiResponse<AnalyticsEvent>> ingestEvent(@RequestBody EventMessage event) {
        AnalyticsEvent savedEvent = eventIngestionService.ingestEvent(event);
        return ResponseEntity.ok(ApiResponse.success(savedEvent, "Event ingested"));
    }

    @GetMapping("/dashboard/overview")
    public ResponseEntity<ApiResponse<DashboardOverview>> getDashboardOverview() {
        // Mock data - in real implementation, aggregate from various metrics
        DashboardOverview overview = DashboardOverview.builder()
                .totalOrders(1500L)
                .totalRevenue(BigDecimal.valueOf(750000.00))
                .slaComplianceRate(metricsService.getMetricValue(MetricType.SLA_COMPLIANCE_RATE, Period.DAILY))
                .activeDrivers(45)
                .avgDeliveryTime(35.5)
                .todayOrders(150L)
                .todayRevenue(BigDecimal.valueOf(75000.00))
                .build();

        return ResponseEntity.ok(ApiResponse.success(overview));
    }

    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<RevenueReport>> getRevenueReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period) {
        RevenueReport report = revenueService.getReportForPeriod(period);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @GetMapping("/revenue/recent")
    public ResponseEntity<ApiResponse<List<RevenueReport>>> getRecentReports() {
        List<RevenueReport> reports = revenueService.getRecentReports();
        return ResponseEntity.ok(ApiResponse.success(reports));
    }

    @GetMapping("/metrics")
    public ResponseEntity<ApiResponse<Double>> getMetric(
            @RequestParam MetricType type,
            @RequestParam Period period) {
        Double value = metricsService.getMetricValue(type, period);
        return ResponseEntity.ok(ApiResponse.success(value));
    }

    @PostMapping("/reports/generate")
    public ResponseEntity<ApiResponse<String>> generateReport() {
        revenueService.generateDailyReport();
        return ResponseEntity.ok(ApiResponse.success("Report generation triggered"));
    }
}
