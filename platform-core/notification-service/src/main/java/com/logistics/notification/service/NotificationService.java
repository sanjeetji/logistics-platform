package com.logistics.notification.service;

import com.logistics.notification.model.*;
import com.logistics.notification.provider.EmailProvider;
import com.logistics.notification.provider.PushNotificationProvider;
import com.logistics.notification.provider.SmsProvider;
import com.logistics.notification.repository.NotificationPreferenceRepository;
import com.logistics.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Service for sending notifications
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final SmsProvider smsProvider;
    private final EmailProvider emailProvider;
    private final PushNotificationProvider pushProvider;

    /**
     * Send notification (async)
     */
    @Async
    @Transactional
    public void sendNotification(String recipientId, RecipientType recipientType, 
                                NotificationChannel channel, String subject, String body,
                                Map<String, Object> metadata) {
        log.info("Sending {} notification to {}", channel, recipientId);

        // Check user preferences
        if (!isChannelEnabled(recipientId, channel)) {
            log.info("Channel {} disabled for user {}", channel, recipientId);
            return;
        }

        // Create notification record
        Notification notification = Notification.builder()
                .notificationId("NOTIF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .recipientId(recipientId)
                .recipientType(recipientType)
                .channel(channel)
                .subject(subject)
                .body(body)
                .metadata(metadata)
                .status(NotificationStatus.PENDING)
                .build();

        notification = notificationRepository.save(notification);

        // Send via appropriate channel
        boolean success = switch (channel) {
            case SMS -> sendSms(recipientId, body);
            case EMAIL -> sendEmail(recipientId, subject, body);
            case PUSH -> sendPush(recipientId, subject, body);
            case WHATSAPP -> sendWhatsApp(recipientId, body);
        };

        // Update status
        if (success) {
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
        } else {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage("Failed to send notification");
        }

        notificationRepository.save(notification);
    }

    private boolean isChannelEnabled(String userId, NotificationChannel channel) {
        return preferenceRepository.findByUserId(userId)
                .map(pref -> switch (channel) {
                    case SMS -> pref.getSmsEnabled();
                    case EMAIL -> pref.getEmailEnabled();
                    case PUSH -> pref.getPushEnabled();
                    case WHATSAPP -> pref.getWhatsappEnabled();
                })
                .orElse(true); // Default to enabled if no preference set
    }

    private boolean sendSms(String recipientId, String message) {
        // In real implementation, fetch phone number from user service
        String phoneNumber = "+1234567890"; // Mock
        return smsProvider.sendSms(phoneNumber, message);
    }

    private boolean sendEmail(String recipientId, String subject, String body) {
        // In real implementation, fetch email from user service
        String email = "user@example.com"; // Mock
        return emailProvider.sendEmail(email, subject, body);
    }

    private boolean sendPush(String recipientId, String title, String body) {
        // In real implementation, fetch device token from user service
        String deviceToken = "mock-device-token";
        return pushProvider.sendPushNotification(deviceToken, title, body);
    }

    private boolean sendWhatsApp(String recipientId, String message) {
        // Mock implementation
        log.info("Sending WhatsApp message to {}: {}", recipientId, message);
        return true;
    }

    /**
     * Get notification by ID
     */
    public Notification getNotification(String notificationId) {
        return notificationRepository.findByNotificationId(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + notificationId));
    }
}
