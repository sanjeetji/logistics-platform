package com.logistics.bff.unified.client.b2c;

import com.logistics.platform.dto.tenant.TenantDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "tenant-service")
public interface TenantServiceClient {
    @GetMapping("/api/v1/tenants/{id}")
    TenantDto getTenantById(@PathVariable("id") String id);
}
