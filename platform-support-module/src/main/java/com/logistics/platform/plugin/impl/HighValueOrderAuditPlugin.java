package com.logistics.platform.plugin.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.platform.plugin.spi.OrderInterceptorPlugin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

/**
 * A concrete plugin implementation that intercepts Order events.
 * If the order value is excessively high, it triggers deep audit logging.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class HighValueOrderAuditPlugin implements OrderInterceptorPlugin {

    // Threshold could theoretically be read from TenantPluginConfig.configJson
    // natively
    private static final BigDecimal HIGH_VALUE_THRESHOLD = new BigDecimal("5000.00");

    @Override
    public String getPluginId() {
        return "high-value-audit-plugin";
    }

    @Override
    public String getName() {
        return "High Value Order Auditing";
    }

    @Override
    public String getDescription() {
        return "Automatically triggers deep compliance audits for orders exceeding predefined currency thresholds.";
    }

    @Override
    public void start() {
        log.info("HighValueOrderAuditPlugin Started.");
    }

    @Override
    public void stop() {
        log.info("HighValueOrderAuditPlugin Stopped.");
    }

    @Override
    public void processOrderEvent(String tenantId, String orderId, String eventType, Map<String, Object> payload) {
        if (!"ORDER_CREATED".equals(eventType) && !"ORDER_UPDATED".equals(eventType)) {
            return;
        }

        try {
            // Extrapolate potential total amount from the generic event payload map
            // natively
            // In a real scenario, this would deserialize to TransportOrderDto or similar
            if (payload.containsKey("orderDto")) {
                Map<String, Object> orderDto = (Map<String, Object>) payload.get("orderDto");
                if (orderDto != null && orderDto.containsKey("totalAmount")) {
                    Object amountObj = orderDto.get("totalAmount");
                    BigDecimal totalAmount = null;

                    if (amountObj instanceof Number) {
                        totalAmount = new BigDecimal(((Number) amountObj).toString());
                    } else if (amountObj instanceof String) {
                        totalAmount = new BigDecimal((String) amountObj);
                    }

                    if (totalAmount != null && totalAmount.compareTo(HIGH_VALUE_THRESHOLD) >= 0) {
                        triggerDeepAudit(tenantId, orderId, totalAmount);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Plugin {} failed to process event for Order {}", getPluginId(), orderId, e);
        }
    }

    private void triggerDeepAudit(String tenantId, String orderId, BigDecimal amount) {
        log.warn("=== HIGH VALUE AUDIT TRIGGERED ===");
        log.warn("[TENANT: {}] Order {} exceeds threshold with value {}. Initiating compliance checks...",
                tenantId, orderId, amount);
        // Here, it would normally persist an Audit log, send email alerts, freeze
        // shipping, etc.
    }
}
