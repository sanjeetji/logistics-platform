package com.logistics.preferences.controller;

import com.logistics.preferences.dto.PreferencesResponse;
import com.logistics.preferences.dto.UpdatePreferencesRequest;
import com.logistics.preferences.service.UserPreferencesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
@Tag(name = "User Preferences", description = "Manage user preferences and settings")
public class PreferencesController {
    
    private final UserPreferencesService preferencesService;
    
    @GetMapping("/{userId}")
    @Operation(summary = "Get user preferences")
    public ResponseEntity<PreferencesResponse> getPreferences(@PathVariable Long userId) {
        PreferencesResponse response = preferencesService.getUserPreferences(userId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{userId}")
    @Operation(summary = "Update user preferences")
    public ResponseEntity<PreferencesResponse> updatePreferences(
            @PathVariable Long userId,
            @RequestBody UpdatePreferencesRequest request) {
        PreferencesResponse response = preferencesService.updatePreferences(userId, request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{userId}/reset")
    @Operation(summary = "Reset preferences to defaults")
    public ResponseEntity<PreferencesResponse> resetToDefaults(@PathVariable Long userId) {
        PreferencesResponse response = preferencesService.resetToDefaults(userId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user preferences")
    public ResponseEntity<Void> deletePreferences(@PathVariable Long userId) {
        preferencesService.deletePreferences(userId);
        return ResponseEntity.noContent().build();
    }
    
    // Convenience endpoints for specific preference categories
    
    @GetMapping("/{userId}/notifications")
    @Operation(summary = "Get notification preferences only")
    public ResponseEntity<PreferencesResponse> getNotificationPreferences(@PathVariable Long userId) {
        // Returns full preferences but UI can extract notification fields
        PreferencesResponse response = preferencesService.getUserPreferences(userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{userId}/theme")
    @Operation(summary = "Get theme preferences only")
    public ResponseEntity<PreferencesResponse> getThemePreferences(@PathVariable Long userId) {
        PreferencesResponse response = preferencesService.getUserPreferences(userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{userId}/privacy")
    @Operation(summary = "Get privacy settings only")
    public ResponseEntity<PreferencesResponse> getPrivacySettings(@PathVariable Long userId) {
        PreferencesResponse response = preferencesService.getUserPreferences(userId);
        return ResponseEntity.ok(response);
    }
}
