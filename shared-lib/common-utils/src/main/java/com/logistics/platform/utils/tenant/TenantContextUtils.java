package com.logistics.platform.utils.tenant;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

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
}
