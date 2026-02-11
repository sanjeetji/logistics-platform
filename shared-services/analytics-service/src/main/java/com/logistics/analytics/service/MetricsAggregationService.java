package com.logistics.analytics.service;

import com.logistics.analytics.model.*;
import com.logistics.analytics.repository.AnalyticsEventRepository;
import com.logistics.analytics.repository.DashboardMetricRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for metrics aggregation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsAggregationService {

    private final AnalyticsEventRepository eventRepository;
    private final DashboardMetricRepository metricRepository;

    /**
     * Aggregate metrics every 5 minutes
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    @Transactional
    public void aggregateMetrics() {
        log.info("Starting metrics aggregation");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime hourAgo = now.minusHours(1);

        // Aggregate order count
        aggregateOrderCount(hourAgo, now);

        // Aggregate SLA compliance
        aggregateSLACompliance(hourAgo, now);

        log.info("Metrics aggregation completed");
    }

    private void aggregateOrderCount(LocalDateTime start, LocalDateTime end) {
        Long orderCount = Optional.ofNullable(eventRepository.countByEventTypeAndTimestampBetween(
                EventType.ORDER_CREATED, start, end)).orElse(0L);

        DashboardMetric metric = DashboardMetric.builder()
                .metricId("METRIC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .metricName("Order Count (Hourly)")
                .metricType(MetricType.ORDER_COUNT)
                .value(orderCount.doubleValue())
                .period(Period.HOURLY)
                .timestamp(LocalDateTime.now())
                .dimensions(new HashMap<>())
                .build();

        metricRepository.save(metric);
        log.debug("Aggregated order count: {}", orderCount);
    }

    private void aggregateSLACompliance(LocalDateTime start, LocalDateTime end) {
        Long totalOrders = Optional.ofNullable(eventRepository.countByEventTypeAndTimestampBetween(
                EventType.ORDER_DELIVERED, start, end)).orElse(0L);

        Long slaBreached = Optional.ofNullable(eventRepository.countByEventTypeAndTimestampBetween(
                EventType.SLA_BREACHED, start, end)).orElse(0L);

        if (totalOrders > 0) {
            double complianceRate = ((totalOrders - slaBreached) / (double) totalOrders) * 100;

            DashboardMetric metric = DashboardMetric.builder()
                    .metricId("METRIC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                    .metricName("SLA Compliance Rate (Hourly)")
                    .metricType(MetricType.SLA_COMPLIANCE_RATE)
                    .value(complianceRate)
                    .period(Period.HOURLY)
                    .timestamp(LocalDateTime.now())
                    .dimensions(new HashMap<>())
                    .build();

            metricRepository.save(metric);
            log.debug("Aggregated SLA compliance: {}%", complianceRate);
        }
    }

    /**
     * Get metric value
     */
    public Double getMetricValue(MetricType metricType, Period period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = switch (period) {
            case HOURLY -> now.minusHours(1);
            case DAILY -> now.minusDays(1);
            case WEEKLY -> now.minusWeeks(1);
            case MONTHLY -> now.minusMonths(1);
        };

        return metricRepository.findByMetricTypeAndTimestampBetween(metricType, start, now)
                .stream()
                .mapToDouble(DashboardMetric::getValue)
                .average()
                .orElse(0.0);
    }
}
