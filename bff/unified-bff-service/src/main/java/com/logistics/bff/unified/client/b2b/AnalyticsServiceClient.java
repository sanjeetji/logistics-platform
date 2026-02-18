package com.logistics.bff.unified.client.b2b;

import com.logistics.platform.dto.analytics.RevenueMetricsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Map;

@FeignClient(name = "analytics-service")
public interface AnalyticsServiceClient {
    @GetMapping("/api/v1/analytics/revenue")
    RevenueMetricsDTO getRevenueAnalytics(@RequestParam(name = "startDate") LocalDate startDate,
            @RequestParam(name = "endDate") LocalDate endDate);

    @GetMapping("/api/v1/analytics/operations")
    Map<String, Object> getOperationalMetrics(@RequestParam(name = "period") String period);

    @GetMapping("/api/v1/analytics/carbon-footprint")
    Map<String, Object> getCarbonFootprint(@RequestParam(name = "tenantId") String tenantId);
}
