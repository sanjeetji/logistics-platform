package com.logistics.shipment.service;

import com.logistics.platform.utils.tenant.TenantFilterAspect;
import com.logistics.shipment.model.Shipment;
import com.logistics.shipment.model.ShipmentStatus;
import com.logistics.shipment.repository.ShipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@Import({ TenantFilterAspect.class, ShipmentService.class, TenantFilterTest.TestConfig.class })
class TenantFilterTest {

    @org.springframework.boot.test.context.TestConfiguration
    @org.springframework.data.jpa.repository.config.EnableJpaAuditing
    @org.springframework.context.annotation.EnableAspectJAutoProxy
    static class TestConfig {
    }

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @MockBean
    private com.logistics.shipment.statemachine.ShipmentStateMachineService stateMachineService;

    @BeforeEach
    void setUp() {
        shipmentRepository.deleteAll();
    }

    @Test
    void shouldFilterShipmentsByTenant() {
        // Tenant A
        setTenantContext("tenant-a");
        Shipment shipmentA = new Shipment();
        shipmentA.setShipmentId(UUID.randomUUID().toString());
        shipmentA.setStatus(ShipmentStatus.CREATED);
        shipmentService.createShipment(shipmentA);

        // Tenant B
        setTenantContext("tenant-b");
        Shipment shipmentB = new Shipment();
        shipmentB.setShipmentId(UUID.randomUUID().toString());
        shipmentB.setStatus(ShipmentStatus.CREATED);
        shipmentService.createShipment(shipmentB);

        // Update Driver ID for A
        setTenantContext("tenant-a");
        String driverId = "driver-1";
        shipmentA.setDriverId(driverId);
        shipmentRepository.save(shipmentA);

        // Update Driver ID for B - MUST BE IN TENANT B CONTEXT
        setTenantContext("tenant-b");
        shipmentB.setDriverId(driverId);
        shipmentRepository.save(shipmentB);

        // Select Tenant A for verification
        setTenantContext("tenant-a");

        // Act
        // Call via SERVICE to ensure Aspect triggers
        List<Shipment> results = shipmentService.getShipmentsByDriver(driverId);

        // Assert
        assertEquals(1, results.size());
        assertEquals("tenant-a", results.get(0).getTenantId());
    }

    private void setTenantContext(String tenantId) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(JwtAuthenticationToken.class);
        Jwt jwt = mock(Jwt.class);

        when(jwt.getClaims()).thenReturn(Map.of("tenant_id", tenantId));
        when(((JwtAuthenticationToken) authentication).getToken()).thenReturn(jwt);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
