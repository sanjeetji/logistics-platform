package com.logistics.payout.client;

import com.logistics.platform.common.dto.payment.PaymentDtos;
import com.logistics.platform.common.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "${app.payment-service.url:http://payment-service:8080}")
public interface PaymentServiceClient {

    @PostMapping("/api/v1/payments/payout")
    ApiResponse<Boolean> processPayout(@RequestBody PaymentDtos.PayoutRequest request);
}
