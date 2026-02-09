package com.logistics.notification.service;

import com.logistics.notification.model.Notification;
import com.logistics.notification.model.NotificationStatus;
import com.logistics.notification.model.NotificationType;
import com.logistics.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void sendNotification(String recipient, String message, NotificationType type) {
        Notification notification = Notification.builder()
                .recipient(recipient)
                .content(message)
                .type(type)
                .status(NotificationStatus.PENDING)
                .build();
        notificationRepository.save(notification);

        try {
            // Placeholder for actual integration (Twilio/SendGrid)
            if (type == NotificationType.EMAIL) {
                log.info("Sending EMAIL to {}: {}", recipient, message);
            } else if (type == NotificationType.SMS) {
                log.info("Sending SMS to {}: {}", recipient, message);
            }

            // Simulating success
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
        } catch (Exception e) {
            log.error("Failed to send notification", e);
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
        }
        notificationRepository.save(notification);
    }
}
