package com.logistics.analytics.streaming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class StreamingAnalyticsApplication {
    public static void main(String[] args) {
        SpringApplication.run(StreamingAnalyticsApplication.class, args);
    }
}
