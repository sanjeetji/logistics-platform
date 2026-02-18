package com.logistics.exception.listener;

import com.logistics.exception.service.ExceptionService;
import com.logistics.platform.event.dto.BusinessExceptionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExceptionEventListener {

    private final ExceptionService exceptionService;

    @KafkaListener(topics = "platform.exceptions", groupId = "exception-management-group")
    public void handleBusinessException(BusinessExceptionEvent event) {
        log.info("Received Business Exception Event: {}", event.getExceptionId());
        try {
            exceptionService.saveException(event);
        } catch (Exception e) {
            log.error("Error processing business exception event: {}", event, e);
        }
    }
}
