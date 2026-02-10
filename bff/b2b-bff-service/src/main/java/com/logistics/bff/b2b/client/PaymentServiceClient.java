package com.logistics.bff.b2b.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.logistics.platform.dto.payment.*;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {
    
    @GetMapping("/api/v1/payments/order/{orderId}")
    PaymentDTO getPaymentByOrder(@PathVariable("orderId") Long orderId);
    
    @GetMapping("/api/v1/payments/order/{orderId}")
    PaymentDTO getPaymentByOrderId(@PathVariable("orderId") String orderId);
    
    @GetMapping("/api/v1/payments/{id}")
    PaymentDTO getPayment(@PathVariable("id") Long id);
}
