package com.logistics.quickdispatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class QuickDispatchApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuickDispatchApplication.class, args);
    }
}
