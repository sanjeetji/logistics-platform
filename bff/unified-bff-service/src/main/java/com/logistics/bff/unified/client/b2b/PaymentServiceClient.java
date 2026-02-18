package com.logistics.bff.unified.client.b2b;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {
    @GetMapping("/api/v1/payments/order/{orderId}")
    Map<String, Object> getPaymentByOrderId(@PathVariable("orderId") String orderId);

    @GetMapping("/api/v1/payments/{id}")
    Map<String, Object> getPaymentById(@PathVariable("id") String id);

    @GetMapping("/api/v1/payments")
    List<Object> getPaymentsByTenant(@RequestParam("tenantId") String tenantId);
}
