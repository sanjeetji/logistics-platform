package com.logistics.bff.unified.controller.mobile;

import com.logistics.bff.unified.service.mobile.MobileSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Mobile Settings Controller
 */
@RestController
@RequestMapping("/api/v1/mobile/settings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Mobile Settings", description = "Settings and configurations for mobile app")
public class MobileSettingsController {

    private final MobileSettingsService settingsService;

    @GetMapping
    @Operation(summary = "Get settings")
    public ResponseEntity<Map<String, Object>> getSettings(@RequestParam String userId) {
        log.info("Mobile settings request for user: {}", userId);
        return ResponseEntity.ok(settingsService.getSettings(userId));
    }

    @PutMapping
    @Operation(summary = "Update settings")
    public ResponseEntity<Map<String, Object>> updateSettings(
            @RequestParam String userId,
            @RequestBody Map<String, Object> settings) {
        log.info("Mobile settings update request for user: {}", userId);
        return ResponseEntity.ok(settingsService.updateSettings(userId, settings));
    }
}
