package com.logistics.bff.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Device Detection Filter - Detects device type from User-Agent header
 */
@Component
@Slf4j
public class DeviceDetectionFilter extends OncePerRequestFilter {

    private static final String DEVICE_TYPE_HEADER = "X-Device-Type";
    private static final String TENANT_ID_HEADER = "X-Tenant-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String userAgent = request.getHeader("User-Agent");
        String deviceType = detectDeviceType(userAgent);
        
        // Add device type to request attributes
        request.setAttribute(DEVICE_TYPE_HEADER, deviceType);
        
        // Extract tenant ID from header or path
        String tenantId = request.getHeader(TENANT_ID_HEADER);
        if (tenantId != null) {
            request.setAttribute(TENANT_ID_HEADER, tenantId);
        }
        
        log.debug("Request from device: {}, tenant: {}, path: {}", deviceType, tenantId, request.getRequestURI());
        
        filterChain.doFilter(request, response);
    }

    private String detectDeviceType(String userAgent) {
        if (userAgent == null) {
            return "UNKNOWN";
        }
        
        userAgent = userAgent.toLowerCase();
        
        if (userAgent.contains("mobile") || userAgent.contains("android") || userAgent.contains("iphone")) {
            return "MOBILE";
        } else if (userAgent.contains("tablet") || userAgent.contains("ipad")) {
            return "TABLET";
        } else {
            return "DESKTOP";
        }
    }
}
