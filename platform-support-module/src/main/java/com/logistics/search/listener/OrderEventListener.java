package com.logistics.search.listener;

import com.logistics.platform.event.dto.OrderStatusChangedEvent;
import com.logistics.search.model.OrderDocument;
import com.logistics.search.service.OrderSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {
    
    private final OrderSearchService orderSearchService;
    
    @KafkaListener(topics = "order-status-changed", groupId = "search-service-group")
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        log.info("Received order status changed event for order: {}", event.getOrderId());
        
        try {
            // Create or update order document in Elasticsearch
            // Note: This is a simplified version. A full implementation would fetch
            // additional order details from order-service or store them in the event
            OrderDocument orderDoc = OrderDocument.builder()
                    .orderId(event.getOrderId())
                    // customerId and driverId would need to be added to the event or fetched separately
                    .status(event.getNewStatus())
                    .updatedAt(event.getTimestamp())
                    .build();
            
            orderSearchService.indexOrder(orderDoc);
            log.info("Successfully indexed order: {}", event.getOrderId());
            
        } catch (Exception e) {
            log.error("Error indexing order: " + event.getOrderId(), e);
        }
    }
}
