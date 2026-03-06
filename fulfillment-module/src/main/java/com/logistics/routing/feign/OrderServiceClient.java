package com.logistics.routing.feign;

import com.logistics.routing.dto.ExternalOrderDto;
import com.logistics.platform.common.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service")
public interface OrderServiceClient {

    @GetMapping("/api/v1/orders/order/{orderId}")
    ApiResponse<ExternalOrderDto> getOrderByOrderId(@PathVariable("orderId") String orderId);

    @GetMapping("/api/v1/orders/{id}")
    ApiResponse<ExternalOrderDto> getOrderById(@PathVariable("id") Long id);
}
