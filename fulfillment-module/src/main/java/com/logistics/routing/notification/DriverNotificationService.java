package com.logistics.routing.notification;

import com.logistics.routing.dto.ReRoutingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Driver Notification Service
 * 
 * Sends notifications to drivers about route changes
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DriverNotificationService {

    private final KafkaTemplate<String, DriverNotificationEvent> kafkaTemplate;

    private static final String NOTIFICATION_TOPIC = "driver-notifications";

    /**
     * Notify driver of route change
     */
    public void notifyDriver(String driverId, String message, ReRoutingResponse reRoutingResponse) {
        
        DriverNotificationEvent notification = DriverNotificationEvent.builder()
            .notificationId(UUID.randomUUID().toString())
            .driverId(driverId)
            .routeId(reRoutingResponse.getRouteId())
            .reRoutingId(reRoutingResponse.getReRoutingId())
            .type(DriverNotificationEvent.NotificationType.ROUTE_UPDATED)
            .message(message)
            .priority(determinePriority(reRoutingResponse))
            .requiresAcknowledgment(true)
            .timestamp(System.currentTimeMillis())
            .build();
        
        try {
            kafkaTemplate.send(NOTIFICATION_TOPIC, driverId, notification);
            log.info("Driver notification sent: driver={}, route={}, message={}", 
                driverId, reRoutingResponse.getRouteId(), message);
        } catch (Exception e) {
            log.error("Failed to send driver notification", e);
            throw e;
        }
    }

    /**
     * Determine notification priority based on re-routing response
     */
    private DriverNotificationEvent.NotificationPriority determinePriority(ReRoutingResponse response) {
        if (response.getStopsAdded() != null && response.getStopsAdded() > 0) {
            return DriverNotificationEvent.NotificationPriority.HIGH;
        }
        if (response.getStopsReordered() != null && response.getStopsReordered() > 3) {
            return DriverNotificationEvent.NotificationPriority.MEDIUM;
        }
        return DriverNotificationEvent.NotificationPriority.LOW;
    }
}
