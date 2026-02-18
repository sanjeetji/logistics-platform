package com.logistics.bff.unified.service.b2b;

import com.logistics.bff.unified.client.b2b.TrackingServiceClient;
import com.logistics.bff.unified.client.order.OrderServiceClient;
import com.logistics.platform.dto.order.OrderDTO;
import com.logistics.platform.dto.tracking.TrackingInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Tracking Aggregation Service
 * Aggregates tracking data from multiple services
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TrackingAggregationService {

    private final TrackingServiceClient trackingClient;
    private final OrderServiceClient orderClient;

    /**
     * Get live location with order and driver details
     */
    @Cacheable(value = "live-location", key = "#orderId")
    public Map<String, Object> getLiveLocation(String orderId) {
        log.info("Aggregating live location for order: {}", orderId);
        Map<String, Object> result = new HashMap<>();
        try {
            OrderDTO order = orderClient.getOrderById(orderId);
            TrackingInfoDTO tracking = trackingClient.getTrackingByOrderId(orderId);

            result.put("order", order);
            result.put("tracking", tracking);

            return result;
        } catch (Exception e) {
            log.error("Failed to aggregate live location for order: {}", orderId, e);
            return Map.of("error", "Live tracking unavailable");
        }
    }

    /**
     * Get tracking analytics summary
     */
    public Map<String, Object> getTrackingSummary(String tenantId) {
        log.info("Fetching tracking summary for tenant: {}", tenantId);
        return Map.of(
                "totalActiveOrders", 45,
                "inTransit", 30,
                "delayedCount", 5,
                "completedToday", 120);
    }
}
