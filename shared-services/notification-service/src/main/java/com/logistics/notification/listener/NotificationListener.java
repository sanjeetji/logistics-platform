package com.logistics.notification.listener;

import com.logistics.notification.model.NotificationType;
import com.logistics.notification.service.NotificationService;
import com.logistics.platform.common.dto.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "${notification.kafka.topic:notification-events}", groupId = "${spring.kafka.consumer.group-id:notification-service}")
    public void consumeNotificationEvent(NotificationEvent event) {
        log.info("Received notification event: {}", event);
        try {
            NotificationType type = NotificationType.EMAIL; // Default
            if (event.getType() != null) {
                try {
                    type = NotificationType.valueOf(event.getType().toUpperCase());
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid notification type: {}, defaulting to EMAIL", event.getType());
                }
            }

            notificationService.sendNotification(event.getRecipient(), event.getContent(), type);
            log.debug("Processed notification for: {}", event.getRecipient());
        } catch (Exception e) {
            log.error("Failed to process notification event: {}", event, e);
        }
    }
}
