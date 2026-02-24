package com.logistics.payout.client;

import com.logistics.platform.common.dto.response.ApiResponse;
import lombok.Builder;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "order-service", url = "${app.order-service.url:http://order-service:8080}")
public interface OrderServiceClient {

    @GetMapping("/api/v1/orders/completed")
    ApiResponse<List<OrderResponse>> getCompletedOrders(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end);

    @Data
    @Builder
    class OrderResponse {
        private String orderId;
        private String driverId;
        private String partnerId;
        private BigDecimal price;
        private LocalDateTime actualDeliveryTime;
    }
}
