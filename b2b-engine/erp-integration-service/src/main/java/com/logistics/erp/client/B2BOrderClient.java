package com.logistics.erp.client;

import com.logistics.platform.common.dto.order.B2BOrderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "b2b-order-service")
public interface B2BOrderClient {

    @PostMapping("/api/v1/b2b-orders/sync")
    void syncOrder(@RequestBody B2BOrderDto orderDto);
}
