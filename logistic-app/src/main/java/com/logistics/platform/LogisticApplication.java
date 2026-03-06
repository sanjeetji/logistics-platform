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
@ComponentScan(basePackages = { "com.logistics", "com.logistics.platform" })
@EntityScan(basePackages = { "com.logistics", "com.logistics.platform" })
@EnableJpaRepositories(basePackages = { "com.logistics", "com.logistics.platform" })
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
