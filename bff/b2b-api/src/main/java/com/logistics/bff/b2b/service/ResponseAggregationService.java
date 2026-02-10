package com.logistics.bff.b2b.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResponseAggregationService {

    private final RestTemplate restTemplate;

    /**
     * Aggregate data from multiple microservices in parallel
     * Reduces chatty calls from frontend
     */
    public Map<String, Object> aggregateOrderDetails(String orderId) {
        log.info("Aggregating order details for: {}", orderId);

        // Parallel calls to multiple services
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
            // TODO: Replace with actual Feign client call
            return Map.of("orderId", orderId, "status", "IN_TRANSIT");
        } catch (Exception e) {
            log.error("Failed to fetch order data", e);
            return Map.of("error", "Failed to fetch order data");
        }
    }

    private Map<String, Object> fetchTrackingData(String orderId) {
        try {
            return Map.of("currentLocation", "Warehouse A", "eta", "2 hours");
        } catch (Exception e) {
            log.error("Failed to fetch tracking data", e);
            return Map.of("error", "Failed to fetch tracking data");
        }
    }

    private Map<String, Object> fetchDriverData(String orderId) {
        try {
            return Map.of("driverName", "John Doe", "phone", "+1234567890");
        } catch (Exception e) {
            log.error("Failed to fetch driver data", e);
            return Map.of("error", "Failed to fetch driver data");
        }
    }

    private Map<String, Object> fetchPaymentData(String orderId) {
        try {
            return Map.of("amount", 150.00, "status", "PAID");
        } catch (Exception e) {
            log.error("Failed to fetch payment data", e);
            return Map.of("error", "Failed to fetch payment data");
        }
    }
}
