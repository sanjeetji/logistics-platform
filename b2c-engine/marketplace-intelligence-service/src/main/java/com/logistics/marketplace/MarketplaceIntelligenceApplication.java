package com.logistics.marketplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MarketplaceIntelligenceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarketplaceIntelligenceApplication.class, args);
	}

}
