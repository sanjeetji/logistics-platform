package com.logistics.platform.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event published when a notification is sent
 * Channels: SMS, EMAIL, PUSH
 * Statuses: SENT, FAILED, DELIVERED
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSentEvent {
    private String notificationId;
    private String recipientId;
    private String channel;
    private String template;
    private String status;
    private LocalDateTime sentAt;
    private String orderId; // if order-related
}
