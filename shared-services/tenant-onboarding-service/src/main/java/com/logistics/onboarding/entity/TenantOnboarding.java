package com.logistics.onboarding.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tenant_onboarding")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantOnboarding {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tenant_id", unique = true)
    private Long tenantId;
    
    @Column(name = "company_name", nullable = false)
    private String companyName;
    
    @Column(name = "company_email", nullable = false)
    private String companyEmail;
    
    @Column(name = "company_phone")
    private String companyPhone;
    
    @Column(name = "contact_person_name")
    private String contactPersonName;
    
    @Column(name = "contact_person_email")
    private String contactPersonEmail;
    
    @Column(name = "business_type")
    private String businessType; // B2B, B2C, BOTH
    
    @Column(name = "industry")
    private String industry;
    
    @Column(name = "expected_monthly_orders")
    private Integer expectedMonthlyOrders;
    
    @Column(name = "country")
    private String country;
    
    @Column(name = "city")
    private String city;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private OnboardingStatus status = OnboardingStatus.INITIATED;
    
    @Column(name = "current_step")
    @Builder.Default
    private Integer currentStep = 1;
    
    @Column(name = "total_steps")
    @Builder.Default
    private Integer totalSteps = 4;
    
    // Subscription details
    @Column(name = "subscription_plan")
    private String subscriptionPlan; // STARTER, GROWTH, ENTERPRISE
    
    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;
    
    @Column(name = "stripe_subscription_id")
    private String stripeSubscriptionId;
    
    @Column(name = "stripe_payment_method_id")
    private String stripePaymentMethodId;
    
    // Trial management
    @Column(name = "is_trial")
    @Builder.Default
    private Boolean isTrial = true;
    
    @Column(name = "trial_start_date")
    private LocalDateTime trialStartDate;
    
    @Column(name = "trial_end_date")
    private LocalDateTime trialEndDate;
    
    @Column(name = "trial_converted")
    @Builder.Default
    private Boolean trialConverted = false;
    
    // Wizard completion tracking
    @Column(name = "company_info_completed")
    @Builder.Default
    private Boolean companyInfoCompleted = false;
    
    @Column(name = "service_config_completed")
    @Builder.Default
    private Boolean serviceConfigCompleted = false;
    
    @Column(name = "payment_setup_completed")
    @Builder.Default
    private Boolean paymentSetupCompleted = false;
    
    @Column(name = "setup_completed")
    @Builder.Default
    private Boolean setupCompleted = false;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    // Email sequence tracking
    @Column(name = "welcome_email_sent")
    @Builder.Default
    private Boolean welcomeEmailSent = false;
    
    @Column(name = "setup_guide_email_sent")
    @Builder.Default
    private Boolean setupGuideEmailSent = false;
    
    @Column(name = "day3_checkin_sent")
    @Builder.Default
    private Boolean day3CheckinSent = false;
    
    @Column(name = "day7_checkin_sent")
    @Builder.Default
    private Boolean day7CheckinSent = false;
    
    @Column(name = "day14_checkin_sent")
    @Builder.Default
    private Boolean day14CheckinSent = false;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Version
    @Column(name = "version")
    private Integer version;
    
    public enum OnboardingStatus {
        INITIATED,
        IN_PROGRESS,
        PAYMENT_PENDING,
        COMPLETED,
        CANCELLED,
        EXPIRED
    }
}
