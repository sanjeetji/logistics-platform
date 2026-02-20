package com.logistics.payout;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.logistics.payout.repository")
@EntityScan(basePackages = "com.logistics.payout.model")
public class PayoutServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PayoutServiceApplication.class, args);
	}

}
