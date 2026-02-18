package com.logistics.bff.unified.client.b2b;

import com.logistics.platform.dto.order.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "b2b-order-service")
public interface B2BOrderClient {
    @PostMapping("/api/v1/b2b/orders")
    OrderDTO createB2BOrder(@RequestBody Map<String, Object> orderRequest);

    @GetMapping("/api/v1/b2b/orders")
    List<OrderDTO> getOrders(@RequestParam("status") String status, @RequestParam("tenantId") String tenantId);
}
