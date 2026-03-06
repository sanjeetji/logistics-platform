package com.logistics.carrier.integration;

import com.logistics.order.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
public class EasyPostCarrierIntegration implements CarrierIntegrationService {

    @Override
    public String getCarrierCode() {
        return "EASYPOST";
    }

    @Override
    public BigDecimal fetchLiveRate(Order order) {
        log.info("Fetching live rates from EasyPost for order {}", order.getOrderId());
        // Mocked outgoing call
        return new BigDecimal("45.50");
    }

    @Override
    public String createShipmentAndGetTracking(Order order) {
        log.info("Generating EasyPost Label for order {}", order.getOrderId());
        // Mocked Label Generation
        return "EZ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Override
    public boolean cancelShipment(String trackingNumber) {
        log.info("Canceling EasyPost Label {}", trackingNumber);
        return true;
    }
}
