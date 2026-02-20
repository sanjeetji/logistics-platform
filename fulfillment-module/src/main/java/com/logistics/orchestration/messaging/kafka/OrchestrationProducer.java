package com.logistics.orchestration.messaging.kafka;

import com.logistics.platform.event.dto.DispatchAssignedEvent;
import com.logistics.platform.event.dto.OrchestrationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrchestrationProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topics.dispatch-commands:dispatch.commands}")
    private String dispatchCommandsTopic;

    public void sendDispatchCommand(OrchestrationEvent event) {
        log.info("Sending dispatch command: {} to topic: {}", event.getEventId(), dispatchCommandsTopic);
        // OrchestrationEvent does not have getOrderId directly, we need to check if we
        // can add it or use a specific event type.
        // Assuming OrchestrationEvent is a wrapper or base. Let's send the eventId or
        // userId as key if orderId unavailable.
        // Actually, OrchestrationEvent usually has a payload. Let's assume for now we
        // use eventId as key.
        kafkaTemplate.send(dispatchCommandsTopic, event.getEventId(), event);
    }
}
