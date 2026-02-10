package com.logistics.dispatch.event;

import com.logistics.dispatch.service.DispatchService;
import com.logistics.platform.event.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DispatchEventConsumer {

    private final DispatchService dispatchService;

    @Bean
    public Consumer<OrderCreatedEvent> orderCreatedConsumer() {
        return event -> {
            log.info("Received OrderCreatedEvent: {}", event.getOrderId());
            try {
                dispatchService.initiateDispatch(event.getOrderDto());
            } catch (Exception e) {
                log.error("Failed to initiate dispatch for order {}: {}", event.getOrderId(), e.getMessage());
            }
        };
    }
}
