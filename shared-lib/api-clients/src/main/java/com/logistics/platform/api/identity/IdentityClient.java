package com.logistics.platform.api.identity;

import com.logistics.platform.common.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "identity-module", path = "/api/v1/features")
public interface IdentityClient {

    @GetMapping("/my-features")
    ApiResponse<MyFeaturesResponse> getMyFeatures(
            @RequestHeader(value = "X-Tenant-Id", required = false) Long headerTenantId,
            @RequestParam(value = "tenantId", required = false) Long paramTenantId);

    record MyFeaturesResponse(Long tenantId, List<String> enabledFeatures) {
    }
}
