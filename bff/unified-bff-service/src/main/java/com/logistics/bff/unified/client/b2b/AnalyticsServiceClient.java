package com.logistics.bff.unified.client.b2b;

import com.logistics.platform.dto.analytics.RevenueMetricsDTO;
import com.logistics.platform.dto.analytics.OperationalMetricsDTO;
import com.logistics.platform.dto.analytics.CarbonFootprintDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@FeignClient(name = "analytics-service")
public interface AnalyticsServiceClient {
    
    @GetMapping("/api/v1/analytics/revenue")
    RevenueMetricsDTO getRevenueAnalytics(@RequestParam LocalDate startDate,
                                          @RequestParam LocalDate endDate);
    
    @GetMapping("/api/v1/analytics/operations")
    OperationalMetricsDTO getOperationalMetrics();
    
    @GetMapping("/api/v1/analytics/carbon-footprint")
    CarbonFootprintDTO getCarbonFootprint();
}
