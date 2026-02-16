package com.logistics.analytics.streaming.service;

import com.logistics.analytics.streaming.model.AnomalyAlert;
import com.logistics.analytics.streaming.model.DriverMetrics;
import com.logistics.analytics.streaming.model.OrderMetrics;
import com.logistics.analytics.streaming.model.RevenueMetrics;
import com.logistics.analytics.streaming.model.SLAMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnomalyDetectionService {

    private final MetricsStorageService metricsStorageService;

    @Value("${analytics.anomaly.threshold-multiplier:2.0}")
    private double thresholdMultiplier;

    @Value("${analytics.anomaly.min-samples:10}")
    private int minSamples;

    // In-memory cache for previous metrics to detect trends
    private OrderMetrics previousOrderMetrics;
    private DriverMetrics previousDriverMetrics;
    private RevenueMetrics previousRevenueMetrics;
    private SLAMetrics previousSLAMetrics;

    @org.springframework.scheduling.annotation.Scheduled(fixedRateString = "${analytics.anomaly.check-interval:60000}")
    public void monitorMetrics() {
        try {
            // Monitor Order Metrics
            OrderMetrics currentOrderMetrics = metricsStorageService.getOrderMetrics();
            if (currentOrderMetrics != null) {
                detectOrderAnomalies(currentOrderMetrics, previousOrderMetrics);
                previousOrderMetrics = currentOrderMetrics;
            }

            // Monitor Driver Metrics
            DriverMetrics currentDriverMetrics = metricsStorageService.getDriverMetrics();
            if (currentDriverMetrics != null) {
                detectDriverAnomalies(currentDriverMetrics, previousDriverMetrics);
                previousDriverMetrics = currentDriverMetrics;
            }

            // Monitor Revenue Metrics
            RevenueMetrics currentRevenueMetrics = metricsStorageService.getRevenueMetrics();
            if (currentRevenueMetrics != null) {
                detectRevenueAnomalies(currentRevenueMetrics, previousRevenueMetrics);
                previousRevenueMetrics = currentRevenueMetrics;
            }

            // Monitor SLA Metrics
            SLAMetrics currentSLAMetrics = metricsStorageService.getSLAMetrics();
            if (currentSLAMetrics != null) {
                detectSLAAnomalies(currentSLAMetrics, previousSLAMetrics);
                previousSLAMetrics = currentSLAMetrics;
            }
        } catch (Exception e) {
            log.error("Error during anomaly detection cycle", e);
        }
    }

    /**
     * Detect anomalies in order metrics
     */
    public void detectOrderAnomalies(OrderMetrics current, OrderMetrics previous) {
        if (previous == null || current.getTotalOrders() < minSamples) {
            return;
        }

        // Check for order spike
        double orderSpike = current.getOrdersPerHour() / Math.max(previous.getOrdersPerHour(), 1.0);
        if (orderSpike > thresholdMultiplier) {
            createAndStoreAlert(
                    "ORDER_SPIKE", "WARNING", "Orders Per Hour",
                    current.getOrdersPerHour(), previous.getOrdersPerHour(),
                    orderSpike, thresholdMultiplier,
                    String.format("Unusual spike in orders: %.0f vs expected %.0f", current.getOrdersPerHour(),
                            previous.getOrdersPerHour()),
                    "Check for system issues or marketing campaigns");
        }

        // Check for high failure rate
        if (current.getFailureRate() > 10.0
                && current.getFailureRate() > previous.getFailureRate() * thresholdMultiplier) {
            createAndStoreAlert(
                    "ERROR_SPIKE", "CRITICAL", "Order Failure Rate",
                    current.getFailureRate(), previous.getFailureRate(),
                    current.getFailureRate() / Math.max(previous.getFailureRate(), 1.0), thresholdMultiplier,
                    String.format("High order failure rate: %.1f%% vs expected %.1f%%", current.getFailureRate(),
                            previous.getFailureRate()),
                    "Investigate system errors and dispatch issues immediately");
        }
    }

    /**
     * Detect anomalies in driver metrics
     */
    public void detectDriverAnomalies(DriverMetrics current, DriverMetrics previous) {
        if (previous == null || current.getTotalDrivers() < minSamples) {
            return;
        }

        // Check for sudden drop in available drivers
        if (previous.getAvailableDrivers() > 10
                && current.getAvailableDrivers() < previous.getAvailableDrivers() / thresholdMultiplier) {
            createAndStoreAlert(
                    "DRIVER_DROP", "WARNING", "Available Drivers",
                    current.getAvailableDrivers(), previous.getAvailableDrivers(),
                    (double) previous.getAvailableDrivers() / Math.max(current.getAvailableDrivers(), 1),
                    thresholdMultiplier,
                    String.format("Sudden drop in available drivers: %d vs previous %d", current.getAvailableDrivers(),
                            previous.getAvailableDrivers()),
                    "Check driver app connectivity or shift schedules");
        }
    }

    /**
     * Detect anomalies in revenue metrics
     */
    public void detectRevenueAnomalies(RevenueMetrics current, RevenueMetrics previous) {
        if (previous == null || current.getTotalTransactions() < minSamples) {
            return;
        }

        // Check for revenue drop
        if (previous.getRevenuePerHour() > 100.0
                && current.getRevenuePerHour() < previous.getRevenuePerHour() / thresholdMultiplier) {
            createAndStoreAlert(
                    "REVENUE_DROP", "CRITICAL", "Revenue Per Hour",
                    current.getRevenuePerHour(), previous.getRevenuePerHour(),
                    previous.getRevenuePerHour() / Math.max(current.getRevenuePerHour(), 1.0), thresholdMultiplier,
                    String.format("Significant drop in revenue rate: %.2f vs previous %.2f",
                            current.getRevenuePerHour(), previous.getRevenuePerHour()),
                    "Verify payment gateway status and checkout flow");
        }
    }

    /**
     * Detect anomalies in SLA metrics
     */
    public void detectSLAAnomalies(SLAMetrics current, SLAMetrics previous) {
        if (previous == null || current.getTotalDeliveries() < minSamples) {
            return;
        }

        // Check for spike in violations
        if (current.getSlaViolations() > previous.getSlaViolations() * thresholdMultiplier
                && current.getSlaViolations() > 5) {
            createAndStoreAlert(
                    "SLA_VIOLATION", "HIGH", "SLA Violations",
                    current.getSlaViolations(), previous.getSlaViolations(),
                    (double) current.getSlaViolations() / Math.max(previous.getSlaViolations(), 1), thresholdMultiplier,
                    String.format("Spike in SLA violations: %d vs previous %d", current.getSlaViolations(),
                            previous.getSlaViolations()),
                    "Analyze route optimization and traffic conditions");
        }
    }

    private void createAndStoreAlert(String type, String severity, String metric, double current, double expected,
            double deviation, double threshold, String description, String recommendation) {
        AnomalyAlert alert = AnomalyAlert.builder()
                .alertId(UUID.randomUUID().toString())
                .alertType(type)
                .severity(severity)
                .timestamp(LocalDateTime.now())
                .metricName(metric)
                .currentValue(current)
                .expectedValue(expected)
                .deviation(deviation)
                .threshold(threshold)
                .description(description)
                .recommendation(recommendation)
                .acknowledged(false)
                .build();

        metricsStorageService.storeAlert(alert);
        log.warn("Anomaly detected [{}]: {}", severity, description);
    }
}
