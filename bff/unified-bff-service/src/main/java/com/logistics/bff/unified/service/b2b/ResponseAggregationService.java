package com.logistics.bff.unified.service.b2b;

import com.logistics.bff.unified.client.OrderServiceClient;
import com.logistics.bff.unified.client.TrackingServiceClient;
import com.logistics.bff.unified.client.FleetServiceClient;
import com.logistics.bff.unified.client.PaymentServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResponseAggregationService {

    private final OrderServiceClient orderClient;
    private final TrackingServiceClient trackingClient;
    private final FleetServiceClient fleetClient;
    private final PaymentServiceClient paymentClient;

    /**
     * Aggregate data from multiple microservices in parallel using Feign clients
     * Reduces chatty calls from frontend
     */
    public Map<String, Object> aggregateOrderDetails(String orderId) {
        log.info("Aggregating order details for: {}", orderId);

        // Parallel calls to multiple services using Feign clients
        CompletableFuture<Map<String, Object>> orderFuture = CompletableFuture.supplyAsync(() -> 
            fetchOrderData(orderId)
        );

        CompletableFuture<Map<String, Object>> trackingFuture = CompletableFuture.supplyAsync(() -> 
            fetchTrackingData(orderId)
        );

        CompletableFuture<Map<String, Object>> driverFuture = CompletableFuture.supplyAsync(() -> 
            fetchDriverData(orderId)
        );

        CompletableFuture<Map<String, Object>> paymentFuture = CompletableFuture.supplyAsync(() -> 
            fetchPaymentData(orderId)
        );

        // Wait for all futures to complete
        CompletableFuture.allOf(orderFuture, trackingFuture, driverFuture, paymentFuture).join();

        // Aggregate results
        Map<String, Object> aggregatedResponse = new HashMap<>();
        try {
            aggregatedResponse.put("order", orderFuture.get());
            aggregatedResponse.put("tracking", trackingFuture.get());
            aggregatedResponse.put("driver", driverFuture.get());
            aggregatedResponse.put("payment", paymentFuture.get());
        } catch (Exception e) {
            log.error("Error aggregating responses", e);
        }

        return aggregatedResponse;
    }

    private Map<String, Object> fetchOrderData(String orderId) {
        try {
            // Use Feign client to call order-service
            var order = orderClient.getOrderById(orderId);
            return Map.of("orderId", orderId, "order", order);
        } catch (Exception e) {
            log.error("Failed to fetch order data", e);
            return Map.of("error", "Failed to fetch order data");
        }
    }

    private Map<String, Object> fetchTrackingData(String orderId) {
        try {
            // Use Feign client to call tracking-service
            var tracking = trackingClient.getTrackingInfo(orderId);
            return Map.of("tracking", tracking);
        } catch (Exception e) {
            log.error("Failed to fetch tracking data", e);
            return Map.of("error", "Failed to fetch tracking data");
        }
    }

    private Map<String, Object> fetchDriverData(String orderId) {
        try {
            // Use Feign client to call fleet-service
            // Note: You'll need to add a method to get driver by order ID
            return Map.of("driverName", "John Doe", "phone", "+1234567890");
        } catch (Exception e) {
            log.error("Failed to fetch driver data", e);
            return Map.of("error", "Failed to fetch driver data");
        }
    }

    private Map<String, Object> fetchPaymentData(String orderId) {
        try {
            // Use Feign client to call payment-service
            var payment = paymentClient.getPaymentByOrderId(orderId);
            return Map.of("payment", payment);
        } catch (Exception e) {
            log.error("Failed to fetch payment data", e);
            return Map.of("error", "Failed to fetch payment data");
        }
    }
}
