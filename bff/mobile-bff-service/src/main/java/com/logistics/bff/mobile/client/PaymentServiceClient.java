package com.logistics.bff.mobile.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {

    @GetMapping("/api/payments/order/{orderId}")
    Object getPaymentByOrderId(@PathVariable String orderId);

    @GetMapping("/api/payments/driver/{driverId}/earnings")
    Object getDriverEarnings(@PathVariable String driverId, @RequestParam String period);

    @PostMapping("/api/payments/process")
    Object processPayment(@RequestBody Object request);
}
