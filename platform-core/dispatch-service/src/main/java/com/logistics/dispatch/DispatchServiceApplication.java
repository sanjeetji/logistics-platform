package com.logistics.dispatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableAsync
@EnableFeignClients(basePackages = { "com.logistics.platform.clients", "com.logistics.platform.api",
		"com.logistics.dispatch.client" })
@org.springframework.context.annotation.ComponentScan(basePackages = { "com.logistics.dispatch",
		"com.logistics.platform" })
public class DispatchServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DispatchServiceApplication.class, args);
	}

}
