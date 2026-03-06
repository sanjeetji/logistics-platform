package com.logistics.platform.utils.tenant;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TenantContextUtils {

    public static String getTenantId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwt = (JwtAuthenticationToken) authentication;
            Map<String, Object> claims = jwt.getToken().getClaims();
            if (claims.containsKey("tenant_id")) {
                return (String) claims.get("tenant_id");
            } else if (claims.containsKey("organization_id")) {
                Object orgId = claims.get("organization_id");
                return orgId != null ? orgId.toString() : null;
            }
        }
        return "default-tenant"; // Fallback
    }

    /**
     * Returns a list of all tenant IDs the current user is authorized to access.
     * In a full implementation, this would include child tenants in the hierarchy.
     */
    public static List<String> getAuthorizedTenantIds() {
        String currentTenant = getTenantId();
        if (currentTenant == null) {
            return Collections.emptyList();
        }
        // For now, identity module only supports single tenant context in JWT.
        // We return a list containing the current tenant.
        return Collections.singletonList(currentTenant);
    }

    /**
     * Returns the data residency region for the current tenant.
     * In a full implementation, this would be fetched from the security context or
     * a cache.
     */
    public static String getDataResidencyRegion() {
        // Placeholder implementation
        return "us-east-1";
    }
}
