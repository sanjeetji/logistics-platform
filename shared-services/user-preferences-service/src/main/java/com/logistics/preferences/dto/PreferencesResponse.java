package com.logistics.preferences.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreferencesResponse {
    
    private Long userId;
    
    // Notification Preferences
    private Boolean emailNotifications;
    private Boolean smsNotifications;
    private Boolean pushNotifications;
    private LocalTime quietHoursStart;
    private LocalTime quietHoursEnd;
    private Boolean orderStatusNotifications;
    private Boolean deliveryNotifications;
    private Boolean paymentNotifications;
    
    // Language & Localization
    private String preferredLanguage;
    private String timezone;
    private String dateFormat;
    private String timeFormat;
    
    // Theme Preferences
    private String theme;
    private String colorScheme;
    private String displayDensity;
    private String fontSize;
    
    // Communication Preferences
    private Boolean marketingEmails;
    private Boolean promotionalSms;
    private Boolean newsletterSubscription;
    private String emailFrequency;
    private Boolean digestEmails;
    
    // Privacy Settings
    private Boolean shareDataWithPartners;
    private Boolean trackLocation;
    private Boolean enableAnalytics;
    private Boolean allowPersonalizedAds;
    private Integer dataRetentionDays;
    
    // Accessibility
    private Boolean screenReaderSupport;
    private Boolean highContrastMode;
    private Boolean keyboardNavigation;
    
    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
