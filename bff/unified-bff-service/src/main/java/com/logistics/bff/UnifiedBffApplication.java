package com.logistics.bff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Unified BFF Service - Consolidates B2B, B2C, and Mobile BFF services
 * 
 * Provides:
 * - B2B API Gateway (path: /api/b2b/*)
 * - B2C API Gateway (path: /api/b2c/*)
 * - Mobile API Gateway (path: /api/mobile/*)
 * - Tenant-aware routing
 * - Device detection
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableCaching
public class UnifiedBffApplication {

    public static void main(String[] args) {
        SpringApplication.run(UnifiedBffApplication.class, args);
    }
}
