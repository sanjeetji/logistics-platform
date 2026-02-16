package com.logistics.preferences.service;

import com.logistics.preferences.dto.PreferencesResponse;
import com.logistics.preferences.dto.UpdatePreferencesRequest;
import com.logistics.preferences.entity.UserPreferences;
import com.logistics.preferences.repository.UserPreferencesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPreferencesService {
    
    private final UserPreferencesRepository preferencesRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    /**
     * Get user preferences, creating default if not exists
     */
    @Transactional
    public PreferencesResponse getUserPreferences(Long userId) {
        UserPreferences preferences = preferencesRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultPreferences(userId));
        
        return mapToResponse(preferences);
    }
    
    /**
     * Update user preferences (partial update)
     */
    @Transactional
    public PreferencesResponse updatePreferences(Long userId, UpdatePreferencesRequest request) {
        UserPreferences preferences = preferencesRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultPreferences(userId));
        
        // Update only non-null fields
        updateFieldsFromRequest(preferences, request);
        
        UserPreferences saved = preferencesRepository.save(preferences);
        
        // Publish preference changed event
        publishPreferenceChangedEvent(userId, saved);
        
        log.info("Updated preferences for user: {}", userId);
        return mapToResponse(saved);
    }
    
    /**
     * Reset preferences to default
     */
    @Transactional
    public PreferencesResponse resetToDefaults(Long userId) {
        preferencesRepository.deleteByUserId(userId);
        UserPreferences preferences = createDefaultPreferences(userId);
        
        log.info("Reset preferences to defaults for user: {}", userId);
        return mapToResponse(preferences);
    }
    
    /**
     * Delete user preferences
     */
    @Transactional
    public void deletePreferences(Long userId) {
        preferencesRepository.deleteByUserId(userId);
        log.info("Deleted preferences for user: {}", userId);
    }
    
    /**
     * Create default preferences for a new user
     */
    private UserPreferences createDefaultPreferences(Long userId) {
        UserPreferences preferences = UserPreferences.builder()
                .userId(userId)
                .build();
        
        return preferencesRepository.save(preferences);
    }
    
    /**
     * Update entity fields from request (only non-null values)
     */
    private void updateFieldsFromRequest(UserPreferences prefs, UpdatePreferencesRequest req) {
        // Notification preferences
        if (req.getEmailNotifications() != null) prefs.setEmailNotifications(req.getEmailNotifications());
        if (req.getSmsNotifications() != null) prefs.setSmsNotifications(req.getSmsNotifications());
        if (req.getPushNotifications() != null) prefs.setPushNotifications(req.getPushNotifications());
        if (req.getQuietHoursStart() != null) prefs.setQuietHoursStart(req.getQuietHoursStart());
        if (req.getQuietHoursEnd() != null) prefs.setQuietHoursEnd(req.getQuietHoursEnd());
        if (req.getOrderStatusNotifications() != null) prefs.setOrderStatusNotifications(req.getOrderStatusNotifications());
        if (req.getDeliveryNotifications() != null) prefs.setDeliveryNotifications(req.getDeliveryNotifications());
        if (req.getPaymentNotifications() != null) prefs.setPaymentNotifications(req.getPaymentNotifications());
        
        // Language
        if (req.getPreferredLanguage() != null) prefs.setPreferredLanguage(req.getPreferredLanguage());
        if (req.getTimezone() != null) prefs.setTimezone(req.getTimezone());
        if (req.getDateFormat() != null) prefs.setDateFormat(req.getDateFormat());
        if (req.getTimeFormat() != null) prefs.setTimeFormat(req.getTimeFormat());
        
        // Theme
        if (req.getTheme() != null) prefs.setTheme(req.getTheme());
        if (req.getColorScheme() != null) prefs.setColorScheme(req.getColorScheme());
        if (req.getDisplayDensity() != null) prefs.setDisplayDensity(req.getDisplayDensity());
        if (req.getFontSize() != null) prefs.setFontSize(req.getFontSize());
        
        // Communication
        if (req.getMarketingEmails() != null) prefs.setMarketingEmails(req.getMarketingEmails());
        if (req.getPromotionalSms() != null) prefs.setPromotionalSms(req.getPromotionalSms());
        if (req.getNewsletterSubscription() != null) prefs.setNewsletterSubscription(req.getNewsletterSubscription());
        if (req.getEmailFrequency() != null) prefs.setEmailFrequency(req.getEmailFrequency());
        if (req.getDigestEmails() != null) prefs.setDigestEmails(req.getDigestEmails());
        
        // Privacy
        if (req.getShareDataWithPartners() != null) prefs.setShareDataWithPartners(req.getShareDataWithPartners());
        if (req.getTrackLocation() != null) prefs.setTrackLocation(req.getTrackLocation());
        if (req.getEnableAnalytics() != null) prefs.setEnableAnalytics(req.getEnableAnalytics());
        if (req.getAllowPersonalizedAds() != null) prefs.setAllowPersonalizedAds(req.getAllowPersonalizedAds());
        if (req.getDataRetentionDays() != null) prefs.setDataRetentionDays(req.getDataRetentionDays());
        
        // Accessibility
        if (req.getScreenReaderSupport() != null) prefs.setScreenReaderSupport(req.getScreenReaderSupport());
        if (req.getHighContrastMode() != null) prefs.setHighContrastMode(req.getHighContrastMode());
        if (req.getKeyboardNavigation() != null) prefs.setKeyboardNavigation(req.getKeyboardNavigation());
    }
    
    /**
     * Map entity to response DTO
     */
    private PreferencesResponse mapToResponse(UserPreferences prefs) {
        return PreferencesResponse.builder()
                .userId(prefs.getUserId())
                .emailNotifications(prefs.getEmailNotifications())
                .smsNotifications(prefs.getSmsNotifications())
                .pushNotifications(prefs.getPushNotifications())
                .quietHoursStart(prefs.getQuietHoursStart())
                .quietHoursEnd(prefs.getQuietHoursEnd())
                .orderStatusNotifications(prefs.getOrderStatusNotifications())
                .deliveryNotifications(prefs.getDeliveryNotifications())
                .paymentNotifications(prefs.getPaymentNotifications())
                .preferredLanguage(prefs.getPreferredLanguage())
                .timezone(prefs.getTimezone())
                .dateFormat(prefs.getDateFormat())
                .timeFormat(prefs.getTimeFormat())
                .theme(prefs.getTheme())
                .colorScheme(prefs.getColorScheme())
                .displayDensity(prefs.getDisplayDensity())
                .fontSize(prefs.getFontSize())
                .marketingEmails(prefs.getMarketingEmails())
                .promotionalSms(prefs.getPromotionalSms())
                .newsletterSubscription(prefs.getNewsletterSubscription())
                .emailFrequency(prefs.getEmailFrequency())
                .digestEmails(prefs.getDigestEmails())
                .shareDataWithPartners(prefs.getShareDataWithPartners())
                .trackLocation(prefs.getTrackLocation())
                .enableAnalytics(prefs.getEnableAnalytics())
                .allowPersonalizedAds(prefs.getAllowPersonalizedAds())
                .dataRetentionDays(prefs.getDataRetentionDays())
                .screenReaderSupport(prefs.getScreenReaderSupport())
                .highContrastMode(prefs.getHighContrastMode())
                .keyboardNavigation(prefs.getKeyboardNavigation())
                .createdAt(prefs.getCreatedAt())
                .updatedAt(prefs.getUpdatedAt())
                .build();
    }
    
    /**
     * Publish preference changed event to Kafka
     */
    private void publishPreferenceChangedEvent(Long userId, UserPreferences preferences) {
        try {
            // Simple event publishing - can be enhanced with proper event contract
            kafkaTemplate.send("user-preferences-changed", userId.toString(), preferences);
            log.debug("Published preference changed event for user: {}", userId);
        } catch (Exception e) {
            log.error("Failed to publish preference changed event", e);
        }
    }
}
