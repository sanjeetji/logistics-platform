package com.logistics.platform.plugin.spi;

import java.util.Map;

/**
 * Specialized plugin interface that listens to order lifecycle events.
 * It provides hooks to inject custom tenant-specific logic when orders
 * are created, updated, or manipulated.
 */
public interface OrderInterceptorPlugin extends LogisticsPlugin {

    /**
     * Hook to process an order event.
     * 
     * @param tenantId  The tenant context ID under which this event fired.
     * @param orderId   The core order ID being processed.
     * @param eventType The specific string classification of the event (e.g.,
     *                  "ORDER_CREATED").
     * @param payload   A map representing the raw event payload (or Order DTO).
     */
    void processOrderEvent(String tenantId, String orderId, String eventType, Map<String, Object> payload);

}
