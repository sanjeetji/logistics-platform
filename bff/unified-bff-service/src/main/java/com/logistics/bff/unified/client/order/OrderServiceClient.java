package com.logistics.bff.unified.client.order;

import com.logistics.platform.dto.order.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Consolidated Order Service Client
 * Shared by B2B, B2C, and Mobile controllers in Unified BFF.
 */
@FeignClient(name = "order-service")
public interface OrderServiceClient {

        // --- Core Operations ---
        @GetMapping("/api/v1/orders/{id}")
        OrderDTO getOrderById(@PathVariable("id") String id);

        @PostMapping("/api/v1/orders")
        OrderDTO createOrder(@RequestBody OrderDTO order);

        @PutMapping("/api/v1/orders/{id}")
        OrderDTO updateOrder(@PathVariable("id") String id, @RequestBody OrderDTO order);

        @PutMapping("/api/v1/orders/{id}/status")
        OrderDTO updateOrderStatus(@PathVariable("id") String id, @RequestParam("status") String status);

        // --- Search & Listing ---
        @GetMapping("/api/v1/orders")
        List<OrderDTO> getOrders(@RequestParam(name = "status", required = false) String status,
                        @RequestParam(name = "customerId", required = false) String customerId,
                        @RequestParam(name = "driverId", required = false) String driverId);

        @GetMapping("/api/v1/orders/count")
        Long getOrderCount(@RequestParam(name = "status", required = false) String status);

        // --- Role-Specific Queries ---
        @GetMapping("/api/v1/orders/customer/{customerId}")
        List<OrderDTO> getCustomerOrders(@PathVariable("customerId") String customerId);

        @GetMapping("/api/v1/orders/driver/{driverId}")
        List<OrderDTO> getDriverOrders(@PathVariable("driverId") String driverId,
                        @RequestParam(name = "status", required = false) String status);

        @GetMapping("/api/v1/orders/tracking/{trackingNumber}")
        OrderDTO getOrderByTrackingNumber(@PathVariable("trackingNumber") String trackingNumber);
}
