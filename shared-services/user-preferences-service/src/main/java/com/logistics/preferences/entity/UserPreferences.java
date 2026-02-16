package com.logistics.preferences.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "user_preferences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferences {
    
    @Id
    @Column(name = "user_id")
    private Long userId;
    
    // Notification Preferences
    @Column(name = "email_notifications")
    @Builder.Default
    private Boolean emailNotifications = true;
    
    @Column(name = "sms_notifications")
    @Builder.Default
    private Boolean smsNotifications = true;
    
    @Column(name = "push_notifications")
    @Builder.Default
    private Boolean pushNotifications = true;
    
    @Column(name = "quiet_hours_start")
    private LocalTime quietHoursStart; // e.g., 22:00
    
    @Column(name = "quiet_hours_end")
    private LocalTime quietHoursEnd; // e.g., 08:00
    
    @Column(name = "order_status_notifications")
    @Builder.Default
    private Boolean orderStatusNotifications = true;
    
    @Column(name = "delivery_notifications")
    @Builder.Default
    private Boolean deliveryNotifications = true;
    
    @Column(name = "payment_notifications")
    @Builder.Default
    private Boolean paymentNotifications = true;
    
    // Language & Localization
    @Column(name = "preferred_language", length = 10)
    @Builder.Default
    private String preferredLanguage = "en"; // ISO 639-1: en, es, fr, de, etc.
    
    @Column(name = "timezone", length = 50)
    @Builder.Default
    private String timezone = "UTC"; // e.g., America/New_York, Europe/London
    
    @Column(name = "date_format", length = 20)
    @Builder.Default
    private String dateFormat = "MM/DD/YYYY"; // MM/DD/YYYY, DD/MM/YYYY, YYYY-MM-DD
    
    @Column(name = "time_format", length = 10)
    @Builder.Default
    private String timeFormat = "12h"; // 12h, 24h
    
    // Theme Preferences
    @Column(name = "theme", length = 20)
    @Builder.Default
    private String theme = "LIGHT"; // LIGHT, DARK, AUTO
    
    @Column(name = "color_scheme", length = 20)
    @Builder.Default
    private String colorScheme = "DEFAULT"; // DEFAULT, BLUE, GREEN, PURPLE
    
    @Column(name = "display_density", length = 20)
    @Builder.Default
    private String displayDensity = "COMFORTABLE"; // COMPACT, COMFORTABLE, SPACIOUS
    
    @Column(name = "font_size", length = 20)
    @Builder.Default
    private String fontSize = "MEDIUM"; // SMALL, MEDIUM, LARGE
    
    // Communication Preferences
    @Column(name = "marketing_emails")
    @Builder.Default
    private Boolean marketingEmails = false;
    
    @Column(name = "promotional_sms")
    @Builder.Default
    private Boolean promotionalSms = false;
    
    @Column(name = "newsletter_subscription")
    @Builder.Default
    private Boolean newsletterSubscription = false;
    
    @Column(name = "email_frequency", length = 20)
    @Builder.Default
    private String emailFrequency = "AS_NEEDED"; // REALTIME, DAILY, WEEKLY, MONTHLY, AS_NEEDED
    
    @Column(name = "digest_emails")
    @Builder.Default
    private Boolean digestEmails = false;
    
    // Privacy Settings
    @Column(name = "share_data_with_partners")
    @Builder.Default
    private Boolean shareDataWithPartners = false;
    
    @Column(name = "track_location")
    @Builder.Default
    private Boolean trackLocation = true;
    
    @Column(name = "enable_analytics")
    @Builder.Default
    private Boolean enableAnalytics = true;
    
    @Column(name = "allow_personalized_ads")
    @Builder.Default
    private Boolean allowPersonalizedAds = false;
    
    @Column(name = "data_retention_days")
    @Builder.Default
    private Integer dataRetentionDays = 365;
    
    // Accessibility
    @Column(name = "screen_reader_support")
    @Builder.Default
    private Boolean screenReaderSupport = false;
    
    @Column(name = "high_contrast_mode")
    @Builder.Default
    private Boolean highContrastMode = false;
    
    @Column(name = "keyboard_navigation")
    @Builder.Default
    private Boolean keyboardNavigation = false;
    
    // Metadata
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Version
    @Column(name = "version")
    private Integer version;
}
