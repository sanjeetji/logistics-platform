package com.logistics.tenant;

import com.logistics.tenant.mapper.TenantMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.logistics.tenant")
public class TenantServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TenantServiceApplication.class, args);
	}

	@Bean
	@ConditionalOnMissingBean
	public TenantMapper tenantMapper() {
		try {
			return (TenantMapper) Class.forName("com.logistics.tenant.mapper.TenantMapperImpl").getDeclaredConstructor()
					.newInstance();
		} catch (Exception e) {
			throw new IllegalStateException("Failed to instantiate TenantMapperImpl via reflection", e);
		}
	}
}
