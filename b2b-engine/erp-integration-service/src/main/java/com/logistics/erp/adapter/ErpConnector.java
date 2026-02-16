package com.logistics.erp.adapter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ErpConnector {
    
    /**
     * Fetch new sales orders from ERP system
     */
    List<Map<String, Object>> fetchNewOrders();

    /**
     * Push inventory update to ERP system
     */
    boolean syncInventory(String productCode, BigDecimal quantity);

    /**
     * Push billing/invoice information to ERP system
     */
    boolean pushInvoice(String orderId, Map<String, Object> billingData);
}
