package com.logistics.driver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class DriverConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
