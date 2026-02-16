package com.logistics.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.order.model.OrderEvent;
import com.logistics.order.repository.OrderEventRepository;
import com.logistics.platform.event.dto.BaseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderEventStore {

    private final OrderEventRepository orderEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void saveEvent(String orderId, BaseEvent event) {
        try {
            OrderEvent orderEvent = OrderEvent.builder()
                    .orderId(orderId)
                    .eventType(event.getEventType())
                    .eventVersion(event.getEventVersion())
                    .payload(objectMapper.writeValueAsString(event))
                    .eventTimestamp(event.getTimestamp())
                    .build();
            orderEventRepository.save(orderEvent);
            log.info("Saved event {} for order {}", event.getEventType(), orderId);
        } catch (Exception e) {
            log.error("Failed to save order event: {}", e.getMessage());
            throw new RuntimeException("Event storage failed", e);
        }
    }

    public List<BaseEvent> getEventsForOrder(String orderId) {
        return orderEventRepository.findByOrderIdOrderByEventTimestampAsc(orderId).stream()
                .map(this::deserializeEvent)
                .collect(Collectors.toList());
    }

    private BaseEvent deserializeEvent(OrderEvent orderEvent) {
        try {
            return objectMapper.readValue(orderEvent.getPayload(), BaseEvent.class);
        } catch (Exception e) {
            log.error("Failed to deserialize order event: {}", e.getMessage());
            return null;
        }
    }
}
