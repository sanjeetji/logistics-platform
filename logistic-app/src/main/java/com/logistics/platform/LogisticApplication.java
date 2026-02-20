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
@ComponentScan(basePackages = {
                "com.logistics.platform",
                "com.logistics.auth",
                "com.logistics.tenant",
                "com.logistics.notification",
                "com.logistics.document",
                "com.logistics.audit",
                "com.logistics.chat",
                "com.logistics.search",
                "com.logistics.integration",
                "com.logistics.sla",
                "com.logistics.rules",
                "com.logistics.order",
                "com.logistics.dispatch",
                "com.logistics.tracking",
                "com.logistics.geo",
                "com.logistics.location",
                "com.logistics.route",
                "com.logistics.fleet",
                "com.logistics.compliance",
                "com.logistics.team",
                "com.logistics.b2b",
                "com.logistics.warehouse",
                "com.logistics.inventory",
                "com.logistics.shipment",
                "com.logistics.payment",
                "com.logistics.billing",
                "com.logistics.pricing",
                "com.logistics.wallet",
                "com.logistics.promocode",
                "com.logistics.loyalty"
})
@EntityScan(basePackages = {
                "com.logistics.platform",
                "com.logistics.auth",
                "com.logistics.tenant",
                "com.logistics.notification",
                "com.logistics.document",
                "com.logistics.audit",
                "com.logistics.chat",
                "com.logistics.integration",
                "com.logistics.sla",
                "com.logistics.rules",
                "com.logistics.order",
                "com.logistics.dispatch",
                "com.logistics.tracking",
                "com.logistics.geo",
                "com.logistics.location",
                "com.logistics.route",
                "com.logistics.fleet",
                "com.logistics.compliance",
                "com.logistics.team",
                "com.logistics.b2b",
                "com.logistics.warehouse",
                "com.logistics.inventory",
                "com.logistics.shipment",
                "com.logistics.payment",
                "com.logistics.billing",
                "com.logistics.pricing",
                "com.logistics.wallet",
                "com.logistics.promocode",
                "com.logistics.loyalty"
})
@EnableJpaRepositories(basePackages = {
                "com.logistics.platform",
                "com.logistics.auth",
                "com.logistics.tenant",
                "com.logistics.notification",
                "com.logistics.document",
                "com.logistics.audit",
                "com.logistics.chat",
                "com.logistics.integration",
                "com.logistics.sla",
                "com.logistics.rules",
                "com.logistics.order",
                "com.logistics.dispatch",
                "com.logistics.tracking",
                "com.logistics.geo",
                "com.logistics.location",
                "com.logistics.route",
                "com.logistics.fleet",
                "com.logistics.compliance",
                "com.logistics.team",
                "com.logistics.b2b",
                "com.logistics.warehouse",
                "com.logistics.inventory",
                "com.logistics.shipment",
                "com.logistics.payment",
                "com.logistics.billing",
                "com.logistics.pricing",
                "com.logistics.wallet",
                "com.logistics.promocode",
                "com.logistics.loyalty"
})
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
