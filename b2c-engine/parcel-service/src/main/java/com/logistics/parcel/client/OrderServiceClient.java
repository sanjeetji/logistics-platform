package com.logistics.parcel.client;

import com.logistics.platform.dto.order.CreateOrderRequest;
import com.logistics.platform.dto.order.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "order-service")
public interface OrderServiceClient {

    @PostMapping("/api/v1/orders")
    OrderDTO createOrder(@RequestBody CreateOrderRequest request);
}
