package com.logistics.tenant.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import lombok.experimental.SuperBuilder;

@Embeddable
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TenantConfig {

    private String billingEmail;
    private String timezone;
    private String currency; // USD, INR
    private boolean dedicatedFleet; // B2B Feature
    private boolean autoDispatch; // Dispatch Strategy config

    // Branding & White-labeling
    private String brandName;
    private String logoUrl;
    private String primaryColor;
    private String secondaryColor;
    private String websiteUrl;
    private String supportPhone;
    private String supportEmail;

    @ElementCollection
    @CollectionTable(name = "tenant_dynamic_settings", joinColumns = @JoinColumn(name = "tenant_id"))
    @MapKeyColumn(name = "setting_key")
    @Column(name = "setting_value")
    @Builder.Default
    private Map<String, String> settings = new HashMap<>();
}
