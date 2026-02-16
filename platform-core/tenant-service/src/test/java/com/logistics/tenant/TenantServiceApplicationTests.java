package com.logistics.tenant;

import com.logistics.tenant.mapper.TenantMapper;
import com.logistics.tenant.service.TenantService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TenantServiceApplicationTests {

	@Autowired
	TenantMapper tenantMapper;

	@Autowired
	TenantService tenantService;

	@Test
	void contextLoads() {
		Assertions.assertNotNull(tenantService, "TenantService should not be null");
		Assertions.assertNotNull(tenantMapper, "TenantMapper should not be null");
	}

}
