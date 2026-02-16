package com.logistics.analytics.streaming.processor;

import com.logistics.analytics.streaming.model.DriverMetrics;
import com.logistics.analytics.streaming.service.MetricsStorageService;
import com.logistics.platform.event.dto.FleetStatusChangedEvent;
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
public class DriverMetricsProcessor {

    private final MetricsStorageService metricsStorageService;

    private static final Duration WINDOW_SIZE = Duration.ofHours(1);

    @Bean
    public Consumer<KStream<String, FleetStatusChangedEvent>> processDriverMetrics() {
        return driverEvents -> {
            driverEvents
                    // Group by a fixed key for global aggregation
                    .groupByKey(Grouped.with(Serdes.String(), null))

                    // Tumbling window of 1 hour
                    .windowedBy(TimeWindows.ofSizeWithNoGrace(WINDOW_SIZE))

                    // Aggregate driver metrics
                    .aggregate(
                            // Initializer
                            () -> {
                                DriverMetrics metrics = new DriverMetrics();
                                metrics.setDriversByCity(new HashMap<>());
                                metrics.setDriversByZone(new HashMap<>());
                                return metrics;
                            },

                            // Aggregator
                            (key, event, metrics) -> {
                                metrics.setTimestamp(LocalDateTime.now());

                                // Track activity count as total drivers proxy for now
                                metrics.setTotalDrivers(metrics.getTotalDrivers() + 1);

                                // Update counts based on current status
                                if (event.getNewStatus() != null) {
                                    switch (event.getNewStatus()) {
                                        case "AVAILABLE":
                                            metrics.setAvailableDrivers(metrics.getAvailableDrivers() + 1);
                                            break;
                                        case "BUSY":
                                        case "EN_ROUTE":
                                        case "AT_PICKUP":
                                        case "AT_DELIVERY":
                                            metrics.setBusyDrivers(metrics.getBusyDrivers() + 1);
                                            break;
                                        case "OFFLINE":
                                            metrics.setOfflineDrivers(metrics.getOfflineDrivers() + 1);
                                            break;
                                    }
                                }

                                // Utilization rate approximation
                                long totalStatusEvents = metrics.getAvailableDrivers() + metrics.getBusyDrivers()
                                        + metrics.getOfflineDrivers();
                                if (totalStatusEvents > 0) {
                                    metrics.setUtilizationRate(
                                            (double) metrics.getBusyDrivers() / totalStatusEvents * 100);
                                }

                                return metrics;
                            },

                            // Materialized store
                            Materialized.with(Serdes.String(), new JsonSerde<>(DriverMetrics.class)))

                    // Convert to stream
                    .toStream()

                    // Map to key-value pairs
                    .map((windowedKey, metrics) -> {
                        String key = "driver_metrics:" + windowedKey.window().startTime().toString();
                        metrics.setWindowStart(windowedKey.window().startTime().toString());
                        metrics.setWindowEnd(windowedKey.window().endTime().toString());
                        return new KeyValue<>(key, metrics);
                    })

                    // Store in Redis
                    .foreach((key, metrics) -> {
                        log.debug("Storing driver metrics: {}", key);
                        metricsStorageService.storeDriverMetrics(metrics);
                    });
        };
    }
}
