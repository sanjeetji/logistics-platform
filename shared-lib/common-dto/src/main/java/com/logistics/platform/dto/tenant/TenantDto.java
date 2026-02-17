package com.logistics.platform.dto.tenant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantDto {
    private Long id;
    private String name;
    private String domain;
    private String industryType;
    private boolean active;
    private String subscriptionPlan;
    private TenantConfigDto config;
}
