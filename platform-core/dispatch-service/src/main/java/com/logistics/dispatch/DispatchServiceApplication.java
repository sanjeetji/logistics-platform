package com.logistics.dispatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = { "com.logistics.platform.clients", "com.logistics.platform.api" })
@org.springframework.context.annotation.ComponentScan(basePackages = { "com.logistics.dispatch",
		"com.logistics.platform" })
public class DispatchServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DispatchServiceApplication.class, args);
	}

}
