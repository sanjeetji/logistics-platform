package com.logistics.analytics.streaming.processor;

import com.logistics.analytics.streaming.model.SLAMetrics;
import com.logistics.analytics.streaming.service.MetricsStorageService;
import com.logistics.platform.event.dto.SLABreachPredictedEvent;
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
import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SLAMetricsProcessor {

    private final MetricsStorageService metricsStorageService;

    private static final Duration WINDOW_SIZE = Duration.ofHours(1);

    @Bean
    public Consumer<KStream<String, SLABreachPredictedEvent>> processSLAMetrics() {
        return slaEvents -> {
            slaEvents
                    // Group by a fixed key for global aggregation
                    .groupByKey(Grouped.with(Serdes.String(), null))

                    // Tumbling window of 1 hour
                    .windowedBy(TimeWindows.ofSizeWithNoGrace(WINDOW_SIZE))

                    // Aggregate SLA metrics
                    .aggregate(
                            // Initializer
                            () -> {
                                SLAMetrics metrics = new SLAMetrics();
                                return metrics; // Just assume default values are 0
                            },

                            // Aggregator
                            (key, event, metrics) -> {
                                metrics.setTimestamp(LocalDateTime.now());

                                // Count violations
                                metrics.setSlaViolations(metrics.getSlaViolations() + 1);

                                // Categorize violations
                                if (event.getRiskLevel() != null) {
                                    switch (event.getRiskLevel()) {
                                        case "CRITICAL":
                                        case "HIGH":
                                            metrics.setCriticalViolations(metrics.getCriticalViolations() + 1);
                                            break;
                                        case "MEDIUM":
                                        case "LOW":
                                            metrics.setMinorViolations(metrics.getMinorViolations() + 1);
                                            break;
                                    }
                                }

                                // Calculate delay stats if available
                                if (event.getCurrentETA() != null && event.getRequiredETA() != null) {
                                    long delayMinutes = Duration.between(event.getRequiredETA(), event.getCurrentETA())
                                            .toMinutes();
                                    if (delayMinutes > 0) {
                                        // Simple incremental average calculation or just summing delays could be
                                        // complex in stream
                                        // For now, let's track max delay
                                        if (delayMinutes > metrics.getMaxDelayMinutes()) {
                                            metrics.setMaxDelayMinutes(delayMinutes);
                                        }
                                    }
                                }

                                return metrics;
                            },

                            // Materialized store
                            Materialized.with(Serdes.String(), new JsonSerde<>(SLAMetrics.class)))

                    // Convert to stream
                    .toStream()

                    // Map to key-value pairs
                    .map((windowedKey, metrics) -> {
                        String key = "sla_metrics:" + windowedKey.window().startTime().toString();
                        metrics.setWindowStart(windowedKey.window().startTime().toString());
                        metrics.setWindowEnd(windowedKey.window().endTime().toString());
                        return new KeyValue<>(key, metrics);
                    })

                    // Store in Redis
                    .foreach((key, metrics) -> {
                        log.debug("Storing SLA metrics: {}", key);
                        metricsStorageService.storeSLAMetrics(metrics);
                    });
        };
    }
}
