package com.logistics.bff.b2b.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.logistics.platform.dto.order.*;

import java.util.List;

@FeignClient(name = "order-service")
public interface OrderServiceClient {
    
    @GetMapping("/api/v1/orders/{id}")
    OrderDTO getOrder(@PathVariable("id") Long id);
    
    @GetMapping("/api/v1/orders/{id}")
    OrderDTO getOrderById(@PathVariable("id") String id);
    
    @GetMapping("/api/v1/orders")
    List<OrderDTO> getOrders(@RequestParam(required = false) String status,
                              @RequestParam(required = false) Long tenantId);
    
    @PostMapping("/api/v1/orders")
    OrderDTO createOrder(@RequestBody CreateOrderRequest request);
    
    @PutMapping("/api/v1/orders/{id}")
    OrderDTO updateOrder(@PathVariable("id") Long id, @RequestBody UpdateOrderRequest request);
    
    @GetMapping("/api/v1/orders/count")
    Long getOrderCount(@RequestParam(required = false) String status);
    
    @GetMapping("/api/v1/orders/customer/{customerId}")
    List<OrderDTO> getCustomerOrders(@PathVariable("customerId") String customerId);
    
    @GetMapping("/api/v1/orders/driver/{driverId}")
    List<OrderDTO> getDriverOrders(@PathVariable("driverId") String driverId, 
                                   @RequestParam(required = false) String status);
    
    @PutMapping("/api/v1/orders/{id}/status")
    OrderDTO updateOrderStatus(@PathVariable("id") String id, 
                               @RequestBody UpdateOrderRequest request);
}
