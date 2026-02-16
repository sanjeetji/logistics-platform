package com.logistics.usermanagement.controller;

import com.logistics.platform.common.dto.ApiResponse;
import com.logistics.usermanagement.entity.UserPreferences;
import com.logistics.usermanagement.repository.UserPreferencesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * User Preferences REST Controller
 */
@RestController
@RequestMapping("/api/v1/preferences")
@RequiredArgsConstructor
public class PreferencesController {

    private final UserPreferencesRepository preferencesRepository;

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<UserPreferences>> getPreferences(@PathVariable Long userId) {
        UserPreferences preferences = preferencesRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Preferences not found for user: " + userId));
        return ResponseEntity.ok(ApiResponse.success(preferences));
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<UserPreferences>> updatePreferences(
            @PathVariable Long userId,
            @RequestBody UserPreferences preferencesUpdate) {
        UserPreferences preferences = preferencesRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Preferences not found for user: " + userId));
        
        // Update preferences
        preferences.setLanguage(preferencesUpdate.getLanguage());
        preferences.setTimezone(preferencesUpdate.getTimezone());
        preferences.setDateFormat(preferencesUpdate.getDateFormat());
        preferences.setTimeFormat(preferencesUpdate.getTimeFormat());
        preferences.setCurrency(preferencesUpdate.getCurrency());
        preferences.setEmailNotifications(preferencesUpdate.getEmailNotifications());
        preferences.setSmsNotifications(preferencesUpdate.getSmsNotifications());
        preferences.setPushNotifications(preferencesUpdate.getPushNotifications());
        preferences.setMarketingEmails(preferencesUpdate.getMarketingEmails());
        preferences.setTheme(preferencesUpdate.getTheme());
        preferences.setCompactView(preferencesUpdate.getCompactView());
        
        UserPreferences saved = preferencesRepository.save(preferences);
        return ResponseEntity.ok(ApiResponse.success(saved, "Preferences updated successfully"));
    }
}
