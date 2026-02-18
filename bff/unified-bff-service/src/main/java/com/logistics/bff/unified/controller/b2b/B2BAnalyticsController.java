package com.logistics.bff.unified.controller.b2b;

import com.logistics.bff.unified.service.b2b.AnalyticsAggregationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * B2B Analytics Controller
 */
@RestController
@RequestMapping("/api/v1/bff/b2b/analytics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "B2B Analytics", description = "Analytics and reporting for B2B clients")
public class B2BAnalyticsController {

        private final AnalyticsAggregationService analyticsService;

        @GetMapping("/dashboard")
        @Operation(summary = "Get dashboard")
        public ResponseEntity<Map<String, Object>> getDashboard(@RequestParam(defaultValue = "MONTHLY") String period) {
                log.info("B2B analytics dashboard request: {}", period);
                return ResponseEntity.ok(analyticsService.getDashboard(period));
        }

        @GetMapping("/revenue")
        @Operation(summary = "Revenue analytics")
        public ResponseEntity<Map<String, Object>> getRevenueAnalytics(
                        @RequestParam String startDate,
                        @RequestParam String endDate) {
                log.info("B2B revenue analytics request: {} to {}", startDate, endDate);
                return ResponseEntity.ok(analyticsService.getRevenueAnalytics(startDate, endDate));
        }

        @GetMapping("/performance")
        @Operation(summary = "Performance metrics")
        public ResponseEntity<Map<String, Object>> getPerformanceMetrics(@RequestParam String metric) {
                log.info("B2B performance metrics request: {}", metric);
                return ResponseEntity.ok(analyticsService.getPerformanceMetrics(metric));
        }

        @GetMapping("/trends")
        @Operation(summary = "Trend analysis")
        public ResponseEntity<Map<String, Object>> getTrendAnalysis(
                        @RequestParam String category,
                        @RequestParam(defaultValue = "30") Integer days) {
                log.info("B2B trend analysis request: {}", category);
                return ResponseEntity.ok(analyticsService.getTrendAnalysis(category, days));
        }
}
