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

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String userAgent = request.getHeader("User-Agent");
        String deviceType = detectDeviceType(userAgent);

        log.debug("Detected device type: {} for User-Agent: {}", deviceType, userAgent);
        request.setAttribute("X-Device-Type", deviceType);

        filterChain.doFilter(request, response);
    }

    private String detectDeviceType(String userAgent) {
        if (userAgent == null) {
            return "UNKNOWN";
        }

        String ua = userAgent.toLowerCase();
        if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone")) {
            return "MOBILE";
        } else if (ua.contains("tablet") || ua.contains("ipad")) {
            return "TABLET";
        } else {
            return "DESKTOP";
        }
    }
}
