package com.logistics.dispatch.event;

import com.logistics.dispatch.service.DispatchService;
import com.logistics.platform.event.dto.DispatchCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DispatchEventConsumer {

    private final DispatchService dispatchService;

    @KafkaListener(topics = "${spring.kafka.topics.dispatch-commands:dispatch.commands}", groupId = "${spring.kafka.consumer.group-id:dispatch-group}")
    public void handleDispatchCommand(DispatchCommand command) {
        log.info("Received DispatchCommand for order: {}", command.getOrderId());
        try {
            // efficient dispatch initiation
            dispatchService.initiateDispatch(command.getOrderId());
        } catch (Exception e) {
            log.error("Failed to process dispatch command for order {}: {}", command.getOrderId(), e.getMessage());
        }
    }
}
