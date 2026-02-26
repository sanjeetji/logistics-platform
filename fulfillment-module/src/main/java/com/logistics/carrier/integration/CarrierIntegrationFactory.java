package com.logistics.carrier.integration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarrierIntegrationFactory {

    private final List<CarrierIntegrationService> registeredCarriers;
    private final Map<String, CarrierIntegrationService> carrierStrategyMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for (CarrierIntegrationService carrier : registeredCarriers) {
            carrierStrategyMap.put(carrier.getCarrierCode().toUpperCase(), carrier);
            log.info("Registered Multi-Carrier Integration Strategy: {}", carrier.getCarrierCode());
        }
    }

    public CarrierIntegrationService getCarrier(String carrierCode) {
        CarrierIntegrationService service = carrierStrategyMap.get(carrierCode.toUpperCase());
        if (service == null) {
            log.warn("Carrier integration for [{}] not found. Using default internal fleet.", carrierCode);
            // In a real system, you might return an internal fleet fallback strategy or
            // throw an Exception
            throw new IllegalArgumentException("Unsupported carrier: " + carrierCode);
        }
        return service;
    }
}
