package com.logistics.bff.b2c;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableCaching
public class B2CBffApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(B2CBffApplication.class, args);
    }
}
