package com.logistics.tenant.mapper;

import com.logistics.platform.dto.tenant.TenantConfigDto;
import com.logistics.platform.dto.tenant.TenantDto;
import com.logistics.tenant.model.Tenant;
import com.logistics.tenant.model.TenantConfig;
import com.logistics.tenant.model.SubscriptionTier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TenantMapper {

    @Mapping(target = "subscriptionPlan", source = "subscriptionTier", qualifiedByName = "tierToString")
    TenantDto toDto(Tenant tenant);

    @Mapping(target = "subscriptionTier", source = "subscriptionPlan", qualifiedByName = "stringToTier")
    Tenant toEntity(TenantDto tenantDto);

    TenantConfigDto toDto(TenantConfig config);

    TenantConfig toEntity(TenantConfigDto configDto);

    List<TenantDto> toDtoList(List<Tenant> tenants);

    /**
     * Converts a SubscriptionTier enum to its String name for the DTO.
     */
    @Named("tierToString")
    default String tierToString(SubscriptionTier tier) {
        return tier == null ? null : tier.name();
    }

    /**
     * Converts a String plan name (case-insensitive) to SubscriptionTier.
     * Valid values: FREE, BRONZE, SILVER, GOLD.
     * Defaults to FREE for null or unrecognised values.
     */
    @Named("stringToTier")
    default SubscriptionTier stringToTier(String plan) {
        if (plan == null || plan.isBlank()) {
            return SubscriptionTier.FREE;
        }
        try {
            return SubscriptionTier.valueOf(plan.toUpperCase());
        } catch (IllegalArgumentException e) {
            return SubscriptionTier.FREE; // Safe fallback
        }
    }
}
