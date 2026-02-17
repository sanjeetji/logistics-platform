package com.logistics.platform.api.order;

import com.logistics.platform.common.dto.order.TransportOrderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "order-service", path = "/api/v1/orders")
public interface OrderClient {

    @GetMapping("/{id}")
    TransportOrderDto getOrderById(@PathVariable("id") Long id);

    @GetMapping
    List<TransportOrderDto> getAllOrders();

    @GetMapping("/demand")
    Integer getDemand();
}
