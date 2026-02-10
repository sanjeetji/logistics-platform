package com.logistics.bff.mobile.client;

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
    
    @GetMapping("/api/v1/orders/driver/{driverId}")
    List<OrderDTO> getDriverOrders(@PathVariable("driverId") String driverId, 
                                   @RequestParam(required = false) String status);
    
    @GetMapping("/api/v1/orders/customer/{customerId}")
    List<OrderDTO> getCustomerOrders(@PathVariable("customerId") String customerId);
    
    @PostMapping("/api/v1/orders")
    OrderDTO createOrder(@RequestBody CreateOrderRequest request);
    
    @PutMapping("/api/v1/orders/{id}/status")
    OrderDTO updateOrderStatus(@PathVariable("id") String orderId, 
                               @RequestBody UpdateOrderRequest request);
}
