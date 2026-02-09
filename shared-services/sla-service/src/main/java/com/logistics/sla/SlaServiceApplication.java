package com.logistics.sla;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.logistics.platform.common.client")
public class SlaServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SlaServiceApplication.class, args);
	}

}
