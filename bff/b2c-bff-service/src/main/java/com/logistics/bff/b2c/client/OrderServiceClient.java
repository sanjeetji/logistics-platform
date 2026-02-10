package com.logistics.bff.b2c.client;

import com.logistics.platform.dto.order.CreateOrderRequest;
import com.logistics.platform.dto.order.OrderDTO;
import com.logistics.platform.dto.order.UpdateOrderRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "order-service")
public interface OrderServiceClient {
    
    @GetMapping("/api/v1/orders/{id}")
    OrderDTO getOrderById(@PathVariable("id") String orderId);
    
    @GetMapping("/api/v1/orders/customer/{customerId}")
    List<OrderDTO> getCustomerOrders(@PathVariable("customerId") String customerId);
    
    @PostMapping("/api/v1/orders")
    OrderDTO createOrder(@RequestBody CreateOrderRequest request);
    
    @PutMapping("/api/v1/orders/{id}")
    OrderDTO updateOrder(@PathVariable("id") String orderId, @RequestBody UpdateOrderRequest request);
    
    @GetMapping("/api/v1/orders/tracking/{trackingNumber}")
    OrderDTO getOrderByTrackingNumber(@PathVariable("trackingNumber") String trackingNumber);
}
