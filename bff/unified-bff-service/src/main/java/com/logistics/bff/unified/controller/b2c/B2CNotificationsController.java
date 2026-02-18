package com.logistics.bff.unified.controller.b2c;

import com.logistics.bff.unified.client.NotificationServiceClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * B2C Notifications Controller
 * Handles notifications for B2C customers
 */
@RestController
@RequestMapping("/api/v1/bff/b2c/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "B2C Notifications", description = "Notifications for B2C customers")
public class B2CNotificationsController {

    private final NotificationServiceClient notificationClient;

    @GetMapping
    @Operation(summary = "Get notifications", description = "Get all notifications for user")
    public ResponseEntity<List<Map<String, Object>>> getNotifications(
            @RequestParam String userId,
            @RequestParam(required = false) Boolean unreadOnly) {
        log.info("Fetching notifications for user: {}, unreadOnly: {}", userId, unreadOnly);
        return ResponseEntity.ok(notificationClient.getUserNotifications(userId, unreadOnly));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark as read", description = "Mark notification as read")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable String id) {
        log.info("Marking notification as read: {}", id);
        return ResponseEntity.ok(notificationClient.markAsRead(id));
    }
}
