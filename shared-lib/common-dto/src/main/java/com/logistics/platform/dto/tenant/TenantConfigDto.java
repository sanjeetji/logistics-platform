package com.logistics.platform.dto.tenant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantConfigDto {
    private String billingEmail;
    private String timezone;
    private String currency;
    private boolean dedicatedFleet;
    private boolean autoDispatch;

    // Branding
    private String brandName;
    private String logoUrl;
    private String primaryColor;
    private String secondaryColor;
    private String websiteUrl;
    private String supportPhone;
    private String supportEmail;
}
