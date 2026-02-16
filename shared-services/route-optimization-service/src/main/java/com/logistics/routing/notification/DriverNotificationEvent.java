package com.logistics.routing.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Driver Notification Event
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverNotificationEvent {

    private String notificationId;
    private String driverId;
    private String routeId;
    private String reRoutingId;
    private NotificationType type;
    private String message;
    private NotificationPriority priority;
    private Long timestamp;
    
    // Notification metadata
    private Boolean requiresAcknowledgment;
    private Long expiresAt;

    public enum NotificationType {
        ROUTE_UPDATED,
        URGENT_DELIVERY_ADDED,
        STOP_REMOVED,
        BREAK_SCHEDULED,
        TRAFFIC_ALERT,
        WEATHER_ALERT
    }

    public enum NotificationPriority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }
}
