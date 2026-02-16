package com.logistics.analytics.streaming.processor;

import com.logistics.analytics.streaming.model.OrderMetrics;
import com.logistics.analytics.streaming.service.MetricsStorageService;
import com.logistics.platform.event.dto.OrderStatusChangedEvent;
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
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class OrderMetricsProcessor {
    
    private final MetricsStorageService metricsStorageService;
    
    private static final Duration WINDOW_SIZE = Duration.ofHours(1);
    private static final Duration HOP_SIZE = Duration.ofMinutes(1);
    
    @Bean
    public Consumer<KStream<String, OrderStatusChangedEvent>> processOrderMetrics() {
        return orderEvents -> {
            orderEvents
                // Group by a fixed key for global aggregation
                .groupByKey(Grouped.with(Serdes.String(), null))
                
                // Tumbling window of 1 hour
                .windowedBy(TimeWindows.ofSizeWithNoGrace(WINDOW_SIZE))
                
                // Aggregate order metrics
                .aggregate(
                    // Initializer
                    OrderMetrics::new,
                    
                    // Aggregator
                    (key, event, metrics) -> {
                        metrics.setTimestamp(LocalDateTime.now());
                        metrics.setTotalOrders(metrics.getTotalOrders() + 1);
                        
                        // Increment status-specific counts
                        switch (event.getNewStatus()) {
                            case "CREATED":
                                metrics.setCreatedOrders(metrics.getCreatedOrders() + 1);
                                break;
                            case "ASSIGNED":
                                metrics.setAssignedOrders(metrics.getAssignedOrders() + 1);
                                break;
                            case "PICKED_UP":
                                metrics.setPickedUpOrders(metrics.getPickedUpOrders() + 1);
                                break;
                            case "IN_TRANSIT":
                                metrics.setInTransitOrders(metrics.getInTransitOrders() + 1);
                                break;
                            case "DELIVERED":
                                metrics.setDeliveredOrders(metrics.getDeliveredOrders() + 1);
                                break;
                            case "CANCELLED":
                                metrics.setCancelledOrders(metrics.getCancelledOrders() + 1);
                                break;
                            case "FAILED":
                                metrics.setFailedOrders(metrics.getFailedOrders() + 1);
                                break;
                        }
                        
                        // Calculate success/failure rates
                        long completed = metrics.getDeliveredOrders() + metrics.getCancelledOrders() + metrics.getFailedOrders();
                        if (completed > 0) {
                            metrics.setSuccessRate((double) metrics.getDeliveredOrders() / completed * 100);
                            metrics.setFailureRate((double) metrics.getFailedOrders() / completed * 100);
                        }
                        
                        // Calculate orders per hour
                        metrics.setOrdersPerHour((double) metrics.getTotalOrders());
                        
                        return metrics;
                    },
                    
                    // Materialized store
                    Materialized.with(Serdes.String(), null)
                )
                
                // Convert to stream
                .toStream()
                
                // Map to key-value pairs
                .map((windowedKey, metrics) -> {
                    String key = "order_metrics:" + windowedKey.window().startTime().toString();
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                    metrics.setWindowStart(windowedKey.window().startTime().toString());
                    metrics.setWindowEnd(windowedKey.window().endTime().toString());
                    return new KeyValue<>(key, metrics);
                })
                
                // Store in Redis
                .foreach((key, metrics) -> {
                    log.debug("Storing order metrics: {}", key);
                    metricsStorageService.storeOrderMetrics(metrics);
                });
        };
    }
}
