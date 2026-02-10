package com.logistics.notification.controller;

import com.logistics.notification.dto.SendNotificationRequest;
import com.logistics.notification.model.Notification;
import com.logistics.notification.model.NotificationPreference;
import com.logistics.notification.model.NotificationTemplate;
import com.logistics.notification.repository.NotificationPreferenceRepository;
import com.logistics.notification.repository.NotificationRepository;
import com.logistics.notification.service.NotificationService;
import com.logistics.notification.service.TemplateService;
import com.logistics.platform.common.dto.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final TemplateService templateService;
    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<String>> sendNotification(@Valid @RequestBody SendNotificationRequest request) {
        notificationService.sendNotification(
                request.getRecipientId(),
                request.getRecipientType(),
                request.getChannel(),
                request.getSubject(),
                request.getBody(),
                request.getMetadata()
        );
        return ResponseEntity.ok(ApiResponse.success("Notification queued for sending"));
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<Notification>> getNotification(@PathVariable String notificationId) {
        Notification notification = notificationService.getNotification(notificationId);
        return ResponseEntity.ok(ApiResponse.success(notification));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Notification>>> getUserNotifications(@PathVariable String userId) {
        List<Notification> notifications = notificationRepository.findByRecipientId(userId);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    @PostMapping("/templates")
    public ResponseEntity<ApiResponse<NotificationTemplate>> createTemplate(@RequestBody NotificationTemplate template) {
        NotificationTemplate created = templateService.createTemplate(template);
        return ResponseEntity.ok(ApiResponse.success(created, "Template created"));
    }

    @GetMapping("/templates")
    public ResponseEntity<ApiResponse<List<NotificationTemplate>>> getTemplates() {
        List<NotificationTemplate> templates = templateService.getActiveTemplates();
        return ResponseEntity.ok(ApiResponse.success(templates));
    }

    @GetMapping("/templates/{templateId}")
    public ResponseEntity<ApiResponse<NotificationTemplate>> getTemplate(@PathVariable String templateId) {
        NotificationTemplate template = templateService.getTemplate(templateId);
        return ResponseEntity.ok(ApiResponse.success(template));
    }

    @PutMapping("/templates/{templateId}")
    public ResponseEntity<ApiResponse<NotificationTemplate>> updateTemplate(
            @PathVariable String templateId,
            @RequestBody NotificationTemplate updates) {
        NotificationTemplate updated = templateService.updateTemplate(templateId, updates);
        return ResponseEntity.ok(ApiResponse.success(updated, "Template updated"));
    }

    @GetMapping("/preferences/{userId}")
    public ResponseEntity<ApiResponse<NotificationPreference>> getPreferences(@PathVariable String userId) {
        NotificationPreference preferences = preferenceRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Preferences not found"));
        return ResponseEntity.ok(ApiResponse.success(preferences));
    }

    @PutMapping("/preferences/{userId}")
    public ResponseEntity<ApiResponse<NotificationPreference>> updatePreferences(
            @PathVariable String userId,
            @RequestBody NotificationPreference preferences) {
        NotificationPreference existing = preferenceRepository.findByUserId(userId)
                .orElse(new NotificationPreference());
        
        existing.setUserId(userId);
        existing.setSmsEnabled(preferences.getSmsEnabled());
        existing.setEmailEnabled(preferences.getEmailEnabled());
        existing.setPushEnabled(preferences.getPushEnabled());
        existing.setWhatsappEnabled(preferences.getWhatsappEnabled());
        
        NotificationPreference saved = preferenceRepository.save(existing);
        return ResponseEntity.ok(ApiResponse.success(saved, "Preferences updated"));
    }
}
