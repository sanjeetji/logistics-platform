package com.logistics.driver.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "driver_profiles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DriverProfile extends BaseEntity {

    @Column(nullable = false, unique = true)
    private Long driverId; // Link to fleet-service Driver

    private String bankAccountNumber;
    
    private String ifscCode;
    
    private String panNumber;
    
    private String aadharNumber;
    
    private String emergencyContactName;
    
    private String emergencyContactPhone;

    @Column(columnDefinition = "text")
    private String preferredVehicleTypes; // JSON array or comma-separated

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private OnboardingStatus onboardingStatus = OnboardingStatus.PENDING;

    @Builder.Default
    @Column(nullable = false)
    private Boolean documentsVerified = false;

    @Column(columnDefinition = "text")
    private String notes;
}
