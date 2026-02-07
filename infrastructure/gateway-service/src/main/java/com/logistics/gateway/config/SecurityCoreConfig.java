package com.logistics.gateway.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "com.logistics.gateway",
        "com.logistics.platform.security"
})
public class SecurityCoreConfig {
}
