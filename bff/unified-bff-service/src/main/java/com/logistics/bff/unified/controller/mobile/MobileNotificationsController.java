package com.logistics.bff.unified.controller.mobile;

import com.logistics.bff.unified.client.mobile.NotificationServiceClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Mobile Notifications Controller
 */
@RestController
@RequestMapping("/api/v1/mobile/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Mobile Notifications", description = "Notifications for mobile app users")
public class MobileNotificationsController {

    private final NotificationServiceClient notificationClient;

    @GetMapping
    @Operation(summary = "Get notifications")
    public ResponseEntity<List<Object>> getNotifications(@RequestParam String userId) {
        log.info("Mobile notifications request for user: {}", userId);
        return ResponseEntity.ok(notificationClient.getNotifications(userId));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark as read")
    public ResponseEntity<Void> markAsRead(@PathVariable String id) {
        log.info("Mobile mark notification as read: {}", id);
        notificationClient.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}
