package com.logistics.tenant.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
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
}
