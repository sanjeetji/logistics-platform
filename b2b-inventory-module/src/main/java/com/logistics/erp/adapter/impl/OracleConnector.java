package com.logistics.erp.adapter.impl;

import com.logistics.erp.adapter.ErpConnector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("oracleConnector")
@Slf4j
public class OracleConnector implements ErpConnector {

    @Override
    public List<Map<String, Object>> fetchNewOrders() {
        log.info("Fetching new orders from Oracle EBS REST API...");
        // Mocking Oracle response
        List<Map<String, Object>> orders = new ArrayList<>();
        
        Map<String, Object> order1 = new HashMap<>();
        order1.put("oracleOrderId", "ORA-445566");
        order1.put("customerNumber", "CUST-ORCL-123");
        order1.put("lineItems", List.of(Map.of("partNumber", "SKU-992", "qty", 200)));
        order1.put("totalValue", new BigDecimal("15000.50"));
        
        orders.add(order1);
        return orders;
    }

    @Override
    public boolean syncInventory(String productCode, BigDecimal quantity) {
        log.info("Syncing inventory to Oracle for Part: {}, Qty: {}", productCode, quantity);
        // Mock success
        return true;
    }

    @Override
    public boolean pushInvoice(String orderId, Map<String, Object> billingData) {
        log.info("Pushing invoice to Oracle for order: {}", orderId);
        // Mock success
        return true;
    }
}
