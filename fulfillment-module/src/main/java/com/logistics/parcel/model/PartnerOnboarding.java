package com.logistics.parcel.model;

import com.logistics.platform.common.dto.TenantAware;
import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "partner_onboardings", indexes = {
        @Index(name = "idx_partner_onboarding_tenant", columnList = "tenant_id"),
        @Index(name = "idx_partner_onboarding_status", columnList = "status")
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PartnerOnboarding extends BaseEntity implements TenantAware {

    @Column(nullable = false)
    private String partnerName;

    @Column(nullable = false)
    private String contactEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OnboardingStatus status;

    @Column(columnDefinition = "text")
    private String verificationDetails;

    @Column(name = "tenant_id")
    private String tenantId;

    private LocalDateTime verifiedAt;

    @Override
    public String getTenantId() {
        return tenantId;
    }

    @Override
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public enum OnboardingStatus {
        PENDING,
        VERIFYING,
        ACTIVE,
        REJECTED
    }
}
