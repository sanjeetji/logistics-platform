package com.logistics.tenant.controller;

import com.logistics.platform.common.dto.response.ApiResponse;
import com.logistics.tenant.dto.TenantDto;
import com.logistics.tenant.mapper.TenantMapper;
import com.logistics.tenant.model.Tenant;
import com.logistics.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;
    private final TenantMapper tenantMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TenantDto>>> getAllTenants() {
        return ResponseEntity.ok(ApiResponse.success(tenantMapper.toDtoList(tenantService.getAllTenants())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TenantDto>> getTenantById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(tenantMapper.toDto(tenantService.getTenantById(id))));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TenantDto>> createTenant(@RequestBody TenantDto tenantDto) {
        Tenant tenant = tenantMapper.toEntity(tenantDto);
        return ResponseEntity.ok(ApiResponse.success(
                tenantMapper.toDto(tenantService.createTenant(tenant)),
                "Tenant created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TenantDto>> updateTenant(@PathVariable Long id,
            @RequestBody TenantDto tenantDto) {
        Tenant tenant = tenantMapper.toEntity(tenantDto);
        return ResponseEntity.ok(ApiResponse.success(
                tenantMapper.toDto(tenantService.updateTenant(id, tenant)),
                "Tenant updated successfully"));
    }
}
