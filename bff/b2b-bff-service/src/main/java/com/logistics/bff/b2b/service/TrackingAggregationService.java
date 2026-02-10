package com.logistics.bff.b2b.service;

import com.logistics.bff.b2b.client.FleetServiceClient;
import com.logistics.bff.b2b.client.OrderServiceClient;
import com.logistics.bff.b2b.client.TrackingServiceClient;
import com.logistics.platform.dto.fleet.DriverDTO;
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
    private final FleetServiceClient fleetClient;

    /**
     * Get live location with order and driver details
     */
    @Cacheable(value = "live-location", key = "#orderId")
    public Map<String, Object> getLiveLocation(String orderId) {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // Get tracking info
            TrackingInfoDTO tracking = trackingClient.getTrackingInfo(orderId);
            result.put("tracking", tracking);
            
            // Get order details
            OrderDTO order = orderClient.getOrderById(orderId);
            result.put("order", Map.of(
                "id", order.getId(),
                "status", order.getStatus(),
                "pickupAddress", order.getPickupAddress(),
                "deliveryAddress", order.getDeliveryAddress()
            ));
            
            // Get driver location if available
            if (order.getDriverId() != null) {
                try {
                    DriverDTO driver = fleetClient.getDriver(Long.parseLong(order.getDriverId()));
                    String driverName = (driver.getFirstName() != null ? driver.getFirstName() : "") + 
                                       " " + (driver.getLastName() != null ? driver.getLastName() : "");
                    result.put("driver", Map.of(
                        "id", driver.getId(),
                        "name", driverName.trim().isEmpty() ? "Unknown" : driverName.trim(),
                        "phoneNumber", driver.getPhoneNumber() != null ? driver.getPhoneNumber() : "N/A",
                        "currentLocation", driver.getCurrentLocation() != null ? driver.getCurrentLocation() : "Unknown"
                    ));
                } catch (Exception e) {
                    log.warn("Failed to fetch driver details: {}", e.getMessage());
                }
            }
            
            return result;
        } catch (Exception e) {
            log.error("Failed to get live location for order: {}", orderId, e);
            throw new RuntimeException("Failed to get live location: " + e.getMessage());
        }
    }

    /**
     * Get tracking analytics
     */
    @Cacheable(value = "tracking-analytics", key = "#startDate + '-' + #endDate")
    public Map<String, Object> getTrackingAnalytics(String startDate, String endDate) {
        try {
            Map<String, Object> analytics = new HashMap<>();
            
            // Mock analytics data - replace with actual service calls
            analytics.put("totalDeliveries", 1250);
            analytics.put("onTimeDeliveries", 1100);
            analytics.put("delayedDeliveries", 150);
            analytics.put("onTimePercentage", 88.0);
            analytics.put("averageDeliveryTime", "45 minutes");
            analytics.put("period", Map.of(
                "start", startDate != null ? startDate : "N/A",
                "end", endDate != null ? endDate : "N/A"
            ));
            
            return analytics;
        } catch (Exception e) {
            log.error("Failed to get tracking analytics", e);
            throw new RuntimeException("Failed to get tracking analytics: " + e.getMessage());
        }
    }
}
