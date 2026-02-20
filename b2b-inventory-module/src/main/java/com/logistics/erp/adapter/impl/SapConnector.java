package com.logistics.erp.adapter.impl;

import com.logistics.erp.adapter.ErpConnector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("sapConnector")
@Slf4j
public class SapConnector implements ErpConnector {

    @Override
    public List<Map<String, Object>> fetchNewOrders() {
        log.info("Fetching new orders from SAP OData API...");
        // Mocking SAP response
        List<Map<String, Object>> orders = new ArrayList<>();

        Map<String, Object> order1 = new HashMap<>();
        order1.put("sapOrderId", "SAP-887123");
        order1.put("clientId", "ENTERPRISE-C1");
        order1.put("items", List.of(Map.of("sku", "SKU-991", "quantity", 500)));
        order1.put("amount", new BigDecimal("25000.00"));

        orders.add(order1);
        return orders;
    }

    @Override
    public boolean syncInventory(String productCode, BigDecimal quantity) {
        log.info("Syncing inventory to SAP for SKU: {}, Quantity: {}", productCode, quantity);
        // Mock success
        return true;
    }

    @Override
    public boolean pushInvoice(String orderId, Map<String, Object> billingData) {
        log.info("Pushing invoice to SAP for order: {}", orderId);
        // Mock success
        return true;
    }
}
