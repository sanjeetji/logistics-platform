package com.logistics.tenant.mapper;

import com.logistics.tenant.dto.TenantConfigDto;
import com.logistics.tenant.dto.TenantDto;
import com.logistics.tenant.model.Tenant;
import com.logistics.tenant.model.TenantConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TenantMapper {

    @Mapping(target = "subscriptionPlan", source = "subscriptionTier")
    TenantDto toDto(Tenant tenant);

    @Mapping(target = "subscriptionTier", source = "subscriptionPlan")
    Tenant toEntity(TenantDto tenantDto);

    TenantConfigDto toDto(TenantConfig config);

    TenantConfig toEntity(TenantConfigDto configDto);

    List<TenantDto> toDtoList(List<Tenant> tenants);
}
