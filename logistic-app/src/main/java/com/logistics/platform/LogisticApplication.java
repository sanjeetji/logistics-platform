package com.logistics.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@ComponentScan(basePackages = "com.logistics")
@EntityScan(basePackages = "com.logistics")
@EnableJpaRepositories(basePackages = "com.logistics")
@EnableElasticsearchRepositories(basePackages = {
                "com.logistics.search"
})
@EnableFeignClients(basePackages = {
                "com.logistics"
})
@EnableScheduling
@EnableCaching
public class LogisticApplication {

        public static void main(String[] args) {
                SpringApplication.run(LogisticApplication.class, args);
        }
}
