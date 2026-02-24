package com.logistics.platform.plugin.service;

import com.logistics.platform.plugin.impl.HighValueOrderAuditPlugin;
import com.logistics.platform.plugin.model.TenantPluginConfig;
import com.logistics.platform.plugin.repository.TenantPluginConfigRepository;
import com.logistics.platform.plugin.spi.LogisticsPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PluginRegistryServiceTest {

    @Mock
    private TenantPluginConfigRepository configRepository;

    private PluginRegistryService registryService;
    private LogisticsPlugin mockAuditPlugin;

    @BeforeEach
    void setUp() {
        mockAuditPlugin = new HighValueOrderAuditPlugin();
        registryService = new PluginRegistryService(List.of(mockAuditPlugin), configRepository);
        registryService.init(); // Manually invoke post-construct
    }

    @Test
    void testInitDiscoversPlugins() {
        List<LogisticsPlugin> allPlugins = registryService.getAllDiscoveredPlugins();
        assertEquals(1, allPlugins.size());
        assertEquals("high-value-audit-plugin", allPlugins.get(0).getPluginId());
    }

    @Test
    void testGetActivePluginsForTenant_WithEnabledPlugin() {
        TenantPluginConfig config = new TenantPluginConfig();
        config.setTenantId("tenant-123");
        config.setPluginId("high-value-audit-plugin");
        config.setActive(true);

        when(configRepository.findByTenantIdAndIsActiveTrue("tenant-123")).thenReturn(List.of(config));

        List<LogisticsPlugin> activePlugins = registryService.getActivePluginsForTenant("tenant-123");

        assertEquals(1, activePlugins.size());
        assertEquals("high-value-audit-plugin", activePlugins.get(0).getPluginId());
    }

    @Test
    void testGetActivePluginsForTenant_WithNoPlugins() {
        when(configRepository.findByTenantIdAndIsActiveTrue("tenant-456")).thenReturn(List.of());

        List<LogisticsPlugin> activePlugins = registryService.getActivePluginsForTenant("tenant-456");

        assertTrue(activePlugins.isEmpty());
    }

    @Test
    void testIsPluginEnabledForTenant() {
        TenantPluginConfig config = new TenantPluginConfig();
        config.setActive(true);
        when(configRepository.findByTenantIdAndPluginId("tenant-123", "high-value-audit-plugin"))
                .thenReturn(Optional.of(config));

        assertTrue(registryService.isPluginEnabledForTenant("tenant-123", "high-value-audit-plugin"));
    }
}
