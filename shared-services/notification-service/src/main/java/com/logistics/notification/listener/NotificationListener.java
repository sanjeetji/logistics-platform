package com.logistics.notification.listener;

import com.logistics.notification.model.NotificationChannel;
import com.logistics.notification.model.RecipientType;
import com.logistics.notification.service.NotificationService;
import com.logistics.platform.event.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "${notification.kafka.topic:notification-events}", groupId = "${spring.kafka.consumer.group-id:notification-service}")
    public void consumeNotificationEvent(NotificationEvent event) {
        log.info("Received notification event: {}", event);
        try {
            NotificationChannel channel = NotificationChannel.EMAIL; // Default
            if (event.getType() != null) {
                try {
                    channel = NotificationChannel.valueOf(event.getType().toUpperCase());
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid notification type: {}, defaulting to EMAIL", event.getType());
                }
            }

            Map<String, Object> metadata = event.getMetaData() != null
                    ? event.getMetaData().entrySet().stream()
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                    : Collections.emptyMap();

            // Defaulting recipient type to CUSTOMER as events typically target users
            notificationService.sendNotification(
                    event.getRecipient(),
                    RecipientType.CUSTOMER,
                    channel,
                    event.getSubject(),
                    event.getContent(),
                    metadata);
            log.debug("Processed notification for: {}", event.getRecipient());
        } catch (Exception e) {
            log.error("Failed to process notification event: {}", event, e);
        }
    }
}
