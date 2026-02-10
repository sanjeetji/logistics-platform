package com.logistics.bff.mobile.controller;

import com.logistics.bff.mobile.service.MobileSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Mobile Settings Controller
 * Handles app settings and configuration for mobile users
 */
@RestController
@RequestMapping("/api/v1/mobile")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Mobile Settings", description = "App settings and configuration")
public class MobileSettingsController {

    private final MobileSettingsService settingsService;

    @GetMapping("/settings")
    @Operation(summary = "Get settings", description = "Get app settings for user")
    public ResponseEntity<Map<String, Object>> getSettings(@RequestParam String userId) {
        log.info("Fetching settings for user: {}", userId);
        return ResponseEntity.ok(settingsService.getSettings(userId));
    }

    @PutMapping("/settings")
    @Operation(summary = "Update settings", description = "Update app settings")
    public ResponseEntity<Map<String, Object>> updateSettings(
            @RequestParam String userId,
            @RequestBody Map<String, Object> settingsData) {
        log.info("Updating settings for user: {}", userId);
        return ResponseEntity.ok(settingsService.updateSettings(userId, settingsData));
    }

    @PostMapping("/feedback")
    @Operation(summary = "Submit feedback", description = "Submit user feedback")
    public ResponseEntity<Map<String, Object>> submitFeedback(@RequestBody Map<String, Object> feedbackData) {
        log.info("Submitting feedback");
        return ResponseEntity.ok(settingsService.submitFeedback(feedbackData));
    }

    @GetMapping("/help")
    @Operation(summary = "Get help", description = "Get help resources and FAQs")
    public ResponseEntity<Map<String, Object>> getHelp(@RequestParam(required = false) String category) {
        log.info("Fetching help resources for category: {}", category);
        return ResponseEntity.ok(settingsService.getHelp(category));
    }
}
