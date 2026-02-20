package com.logistics.payment.adapter;

import com.logistics.platform.common.dto.payment.PaymentDtos;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GatewayFactory {

    private final Map<PaymentDtos.GatewayType, PaymentGateway> gateways;

    public GatewayFactory(List<PaymentGateway> gatewayList) {
        this.gateways = gatewayList.stream()
                .collect(Collectors.toMap(PaymentGateway::getGatewayType, Function.identity()));
    }

    public PaymentGateway getGateway(PaymentDtos.GatewayType type) {
        return Optional.ofNullable(gateways.get(type))
                .orElseThrow(() -> new RuntimeException("No payment gateway found for type: " + type));
    }
}
