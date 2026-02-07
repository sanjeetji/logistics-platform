package com.logistics.tenant.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "tenants")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE tenants SET deleted = true WHERE id=?")
@SQLRestriction("deleted=false")
public class Tenant extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String domain; // e.g., acme.logistics.com

    @Column(name = "industry_type")
    private String industryType; // RETAIL, PHARMA, FMCG

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "subscription_plan")
    private String subscriptionPlan; // FREE, PREMIUM, ENTERPRISE

    @Embedded
    private TenantConfig config;

    @Column(nullable = false)
    @Builder.Default
    private boolean deleted = false;
}
