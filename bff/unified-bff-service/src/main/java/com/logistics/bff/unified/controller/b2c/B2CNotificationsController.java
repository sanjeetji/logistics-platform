package com.logistics.bff.unified.controller.b2c;

import com.logistics.bff.unified.client.b2c.NotificationServiceClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * B2C Notifications Controller
 */
@RestController
@RequestMapping("/api/v1/bff/b2c/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "B2C Notifications", description = "Notifications for B2C customers")
public class B2CNotificationsController {

    private final NotificationServiceClient notificationClient;

    @GetMapping
    @Operation(summary = "Get notifications")
    public ResponseEntity<List<Object>> getNotifications(
            @RequestParam String userId,
            @RequestParam(required = false) Boolean unreadOnly) {
        log.info("B2C notifications request for user: {}", userId);
        return ResponseEntity.ok(notificationClient.getNotifications(userId));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark as read")
    public ResponseEntity<Void> markAsRead(@PathVariable String id) {
        log.info("B2C mark notification as read: {}", id);
        notificationClient.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}
