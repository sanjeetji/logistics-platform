package com.logistics.tenant.model;

import com.logistics.platform.common.dto.enums.BusinessModel;
import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tenants")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE tenants SET deleted = true WHERE id=?")
@SQLRestriction("deleted=false")
public class Tenant extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "data_residency_region")
    private String dataResidencyRegion; // e.g., us-east-1, eu-central-1

    @Column(nullable = false, unique = true)
    private String domain; // e.g., acme.logistics.com

    @Column(name = "industry_type")
    private String industryType; // RETAIL, PHARMA, FMCG

    @Enumerated(EnumType.STRING)
    @Column(name = "business_model", nullable = false)
    @Builder.Default
    private BusinessModel businessModel = BusinessModel.B2B;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "subscription_tier")
    @Builder.Default
    private SubscriptionTier subscriptionTier = SubscriptionTier.FREE;

    @Embedded
    private TenantConfig config;

    @Column(nullable = false)
    @Builder.Default
    private boolean deleted = false;
}
