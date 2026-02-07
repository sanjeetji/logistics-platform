package com.logistics.tenant.dto;

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
}
