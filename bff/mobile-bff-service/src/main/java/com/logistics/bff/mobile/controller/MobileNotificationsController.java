package com.logistics.bff.mobile.controller;

import com.logistics.bff.mobile.client.NotificationServiceClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Mobile Notifications Controller
 * Handles notifications for mobile app users
 */
@RestController
@RequestMapping("/api/v1/mobile/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Mobile Notifications", description = "Notifications for mobile app")
public class MobileNotificationsController {

    private final NotificationServiceClient notificationClient;

    @GetMapping
    @Operation(summary = "Get notifications", description = "Get all notifications for mobile user")
    public ResponseEntity<List<Map<String, Object>>> getNotifications(
            @RequestParam String userId,
            @RequestParam(required = false) Boolean unreadOnly) {
        log.info("Mobile: Fetching notifications for user: {}", userId);
        return ResponseEntity.ok(notificationClient.getUserNotifications(userId, unreadOnly));
    }
}
