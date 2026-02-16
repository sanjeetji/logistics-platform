package com.logistics.routing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Route Optimization Service - Advanced routing with Google OR-Tools
 * 
 * Features:
 * - Google OR-Tools VRP/TSP solver
 * - Real-time traffic integration
 * - ML-powered ETA prediction
 * - Dynamic re-routing (6 triggers)
 * - Multi-objective optimization (cost/speed/green)
 * - What-if analysis & simulation
 * - Zone-based routing
 * - Driver skill matching
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableKafka
@EnableCaching
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
public class RouteOptimizationApplication {

    public static void main(String[] args) {
        SpringApplication.run(RouteOptimizationApplication.class, args);
    }
}
