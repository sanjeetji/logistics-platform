package com.logistics.notification.controller;

import com.logistics.platform.common.dto.response.ApiResponse;
import com.logistics.notification.model.NotificationType;
import com.logistics.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<String>> sendNotification(
            @RequestParam String recipient,
            @RequestParam String message,
            @RequestParam NotificationType type) {
        notificationService.sendNotification(recipient, message, type);
        return ResponseEntity.ok(ApiResponse.success("Notification queued successfully"));
    }
}
