package com.logistics.usermanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * User Preferences entity - consolidated from user-preferences-service
 */
@Entity
@Table(name = "user_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class UserPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // --- Language & Localization ---
    @Column(nullable = false, length = 10)
    @Builder.Default
    private String language = "en";

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String timezone = "UTC";

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String dateFormat = "yyyy-MM-dd";

    @Column(nullable = false, length = 10)
    @Builder.Default
    private String timeFormat = "HH:mm:ss"; // 12h, 24h

    @Column(nullable = false, length = 10)
    @Builder.Default
    private String currency = "USD";

    // --- Notification Preferences ---
    @Column(nullable = false)
    @Builder.Default
    private Boolean emailNotifications = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean smsNotifications = false;

    @Column(nullable = false)
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

    // --- Communication Preferences ---
    @Column(nullable = false)
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

    // --- Theme Preferences ---
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String theme = "LIGHT"; // LIGHT, DARK, AUTO

    @Column(name = "color_scheme", length = 20)
    @Builder.Default
    private String colorScheme = "DEFAULT";

    @Column(nullable = false)
    @Builder.Default
    private Boolean compactView = false; // displayDensity replacement

    @Column(name = "font_size", length = 20)
    @Builder.Default
    private String fontSize = "MEDIUM";

    // --- Privacy Settings ---
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

    // --- Accessibility ---
    @Column(name = "screen_reader_support")
    @Builder.Default
    private Boolean screenReaderSupport = false;

    @Column(name = "high_contrast_mode")
    @Builder.Default
    private Boolean highContrastMode = false;

    @Column(name = "keyboard_navigation")
    @Builder.Default
    private Boolean keyboardNavigation = false;

    // --- Audit ---
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
