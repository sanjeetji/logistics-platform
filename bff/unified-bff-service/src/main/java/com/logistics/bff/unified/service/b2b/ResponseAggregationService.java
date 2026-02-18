package com.logistics.bff.unified.service.b2b;

import com.logistics.bff.unified.client.b2b.FleetServiceClient;
import com.logistics.bff.unified.client.b2b.PaymentServiceClient;
import com.logistics.bff.unified.client.b2b.TrackingServiceClient;
import com.logistics.bff.unified.client.order.OrderServiceClient;
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
         * Aggregate data from multiple microservices in parallel
         */
        public Map<String, Object> aggregateOrderDetails(String orderId) {
                log.info("Aggregating order details for: {}", orderId);

                CompletableFuture<Map<String, Object>> orderFuture = CompletableFuture
                                .supplyAsync(() -> fetchOrderData(orderId));

                CompletableFuture<Map<String, Object>> trackingFuture = CompletableFuture
                                .supplyAsync(() -> fetchTrackingData(orderId));

                CompletableFuture<Map<String, Object>> driverFuture = CompletableFuture
                                .supplyAsync(() -> fetchDriverData(orderId));

                CompletableFuture<Map<String, Object>> paymentFuture = CompletableFuture
                                .supplyAsync(() -> fetchPaymentData(orderId));

                CompletableFuture.allOf(orderFuture, trackingFuture, driverFuture, paymentFuture).join();

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
                        var order = orderClient.getOrderById(orderId);
                        return Map.of("orderId", orderId, "order", order);
                } catch (Exception e) {
                        log.error("Failed to fetch order data: {}", orderId, e);
                        return Map.of("error", "Order data unavailable");
                }
        }

        private Map<String, Object> fetchTrackingData(String orderId) {
                try {
                        // Use Feign client to call tracking-service
                        var tracking = trackingClient.getTrackingByOrderId(orderId);
                        return Map.of("tracking", tracking);
                } catch (Exception e) {
                        log.error("Failed to fetch tracking data: {}", orderId, e);
                        return Map.of("error", "Tracking data unavailable");
                }
        }

        private Map<String, Object> fetchDriverData(String orderId) {
                // Implementation would typically get driver id from order first
                return Map.of("driverId", "DRV-123", "name", "John Doe");
        }

        private Map<String, Object> fetchPaymentData(String orderId) {
                try {
                        // Use Feign client to call payment-service
                        var payment = paymentClient.getPaymentById(orderId);
                        return Map.of("payment", payment);
                } catch (Exception e) {
                        log.error("Failed to fetch payment data: {}", orderId, e);
                        return Map.of("error", "Payment data unavailable");
                }
        }
}
