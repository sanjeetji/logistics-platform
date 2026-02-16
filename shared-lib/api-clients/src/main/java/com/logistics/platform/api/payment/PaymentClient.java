package com.logistics.platform.api.payment;

import com.logistics.platform.common.dto.payment.PaymentDtos;
import com.logistics.platform.common.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", path = "/api/v1/payments")
public interface PaymentClient {

    @PostMapping("/process")
    Void processPayment(@RequestBody PaymentDtos.PaymentRequest request);

    @PostMapping("/payout")
    ApiResponse<Boolean> processPayout(@RequestBody PaymentDtos.PayoutRequest request);
}
