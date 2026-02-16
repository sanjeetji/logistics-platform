package com.logistics.preferences;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class UserPreferencesApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserPreferencesApplication.class, args);
    }
}
