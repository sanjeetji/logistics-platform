package com.logistics.bff.unified.client.mobile;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {
    @GetMapping("/api/payments/order/{orderId}")
    Map<String, Object> getPaymentByOrderId(@PathVariable("orderId") String orderId);

    @GetMapping("/api/payments/driver/{driverId}/earnings")
    Map<String, Object> getDriverEarnings(@PathVariable("driverId") String driverId);

    @PostMapping("/api/payments/process")
    Map<String, Object> processPayment(@RequestBody Map<String, Object> payment);
}
