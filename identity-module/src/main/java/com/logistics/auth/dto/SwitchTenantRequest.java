package com.logistics.auth.dto;

import jakarta.validation.constraints.NotNull;

public class SwitchTenantRequest {
    @NotNull(message = "Target Organization ID is required")
    private Long targetOrganizationId;

    public SwitchTenantRequest() {
    }

    public SwitchTenantRequest(Long targetOrganizationId) {
        this.targetOrganizationId = targetOrganizationId;
    }

    public Long getTargetOrganizationId() {
        return targetOrganizationId;
    }

    public void setTargetOrganizationId(Long targetOrganizationId) {
        this.targetOrganizationId = targetOrganizationId;
    }
}
