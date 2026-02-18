package com.logistics.platform.client.b2b;

import com.logistics.platform.common.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "b2b-order-service", url = "${application.config.b2b-order-service-url}")
public interface B2BOrderServiceClient {

    @PostMapping("/api/v1/b2b/orders")
    ApiResponse<Object> createOrder(@RequestBody Object request);
}
