package com.logistics.shipment.service;

import com.logistics.platform.utils.tenant.TenantListener;
import com.logistics.shipment.model.Shipment;
import com.logistics.shipment.model.ShipmentStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenantIsolationTest {

    private TenantListener tenantListener;
    private MockedStatic<SecurityContextHolder> securityContextHolderMock;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        tenantListener = new TenantListener();
        securityContextHolderMock = org.mockito.Mockito.mockStatic(SecurityContextHolder.class);
        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
    }

    @AfterEach
    void tearDown() {
        securityContextHolderMock.close();
    }

    @Test
    void shouldPopulateTenantIdFromSecurityContext() {
        // Arrange
        String tenantId = "tenant-123";
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaims()).thenReturn(Map.of("tenant_id", tenantId));
        JwtAuthenticationToken jwtAuth = new JwtAuthenticationToken(jwt);

        when(securityContext.getAuthentication()).thenReturn(jwtAuth);

        Shipment shipment = new Shipment();
        shipment.setStatus(ShipmentStatus.CREATED);

        // Act
        tenantListener.setTenantId(shipment);

        // Assert
        assertEquals(tenantId, shipment.getTenantId());
    }

    @Test
    void shouldNotFailUsingFallbackOrNullVerifyLogic() {
        // Setup for no auth
        when(securityContext.getAuthentication()).thenReturn(null);

        Shipment shipment = new Shipment();
        tenantListener.setTenantId(shipment);

        // Should default to something or remain null based on listener logic
        // Updated listener has a fall back to "default-tenant" for testing
        assertEquals("default-tenant", shipment.getTenantId());
    }
}
