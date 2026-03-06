package com.logistics.platform.client.order;

import com.logistics.platform.common.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service", url = "${application.config.order-service-url:http://localhost:8080}")
public interface OrderServiceClient {

    @GetMapping("/api/v1/orders/order/{orderId}")
    ApiResponse<Object> getOrderByOrderId(@PathVariable("orderId") String orderId);
}
