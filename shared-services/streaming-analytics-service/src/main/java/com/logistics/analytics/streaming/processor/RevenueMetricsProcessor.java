package com.logistics.analytics.streaming.processor;

import com.logistics.analytics.streaming.model.RevenueMetrics;
import com.logistics.analytics.streaming.service.MetricsStorageService;
import com.logistics.platform.event.dto.PaymentCompletedEvent;
import org.springframework.kafka.support.serializer.JsonSerde;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RevenueMetricsProcessor {

    private final MetricsStorageService metricsStorageService;

    private static final Duration WINDOW_SIZE = Duration.ofHours(1);

    @Bean
    public Consumer<KStream<String, PaymentCompletedEvent>> processRevenueMetrics() {
        return paymentEvents -> {
            paymentEvents
                    // Group by a fixed key for global aggregation
                    .groupByKey(Grouped.with(Serdes.String(), null))

                    // Tumbling window of 1 hour
                    .windowedBy(TimeWindows.ofSizeWithNoGrace(WINDOW_SIZE))

                    // Aggregate revenue metrics
                    .aggregate(
                            // Initializer
                            () -> {
                                RevenueMetrics metrics = new RevenueMetrics();
                                metrics.setRevenueByCity(new HashMap<>());
                                metrics.setRevenueByPaymentMethod(new HashMap<>());
                                return metrics;
                            },

                            // Aggregator
                            (key, event, metrics) -> {
                                metrics.setTimestamp(LocalDateTime.now());

                                // Update totals
                                metrics.setTotalTransactions(metrics.getTotalTransactions() + 1);
                                metrics.setSuccessfulTransactions(metrics.getSuccessfulTransactions() + 1);

                                double amount = event.getAmount() != null ? event.getAmount().doubleValue() : 0.0;
                                metrics.setTotalRevenue(metrics.getTotalRevenue() + amount);
                                metrics.setGrossRevenue(metrics.getGrossRevenue() + amount); // Assuming gross = total
                                                                                             // for now
                                metrics.setNetRevenue(metrics.getNetRevenue() + amount); // Assuming net = gross for now

                                // Update averages
                                if (metrics.getSuccessfulTransactions() > 0) {
                                    metrics.setAverageOrderValue(
                                            metrics.getTotalRevenue() / metrics.getSuccessfulTransactions());
                                }

                                // Update by payment method
                                if (event.getPaymentMethod() != null) {
                                    metrics.getRevenueByPaymentMethod().merge(
                                            event.getPaymentMethod(),
                                            amount,
                                            Double::sum);
                                }

                                return metrics;
                            },

                            // Materialized store
                            Materialized.with(Serdes.String(), new JsonSerde<>(RevenueMetrics.class)))

                    // Convert to stream
                    .toStream()

                    // Map to key-value pairs
                    .map((windowedKey, metrics) -> {
                        String key = "revenue_metrics:" + windowedKey.window().startTime().toString();
                        metrics.setWindowStart(windowedKey.window().startTime().toString());
                        metrics.setWindowEnd(windowedKey.window().endTime().toString());
                        return new KeyValue<>(key, metrics);
                    })

                    // Store in Redis
                    .foreach((key, metrics) -> {
                        log.debug("Storing revenue metrics: {}", key);
                        metricsStorageService.storeRevenueMetrics(metrics);
                    });
        };
    }
}
