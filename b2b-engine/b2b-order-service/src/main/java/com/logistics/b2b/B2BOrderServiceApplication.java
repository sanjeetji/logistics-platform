package com.logistics.b2b;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.logistics.platform.client")
public class B2BOrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(B2BOrderServiceApplication.class, args);
    }
}
