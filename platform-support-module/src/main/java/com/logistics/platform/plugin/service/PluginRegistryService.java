package com.logistics.platform.plugin.service;

import com.logistics.platform.plugin.model.TenantPluginConfig;
import com.logistics.platform.plugin.repository.TenantPluginConfigRepository;
import com.logistics.platform.plugin.spi.LogisticsPlugin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Discovers and manages the lifecycle of LogisticsPlugins registered in the
 * application context.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PluginRegistryService {

    private final List<LogisticsPlugin> availablePlugins;
    private final TenantPluginConfigRepository configRepository;

    // Map of Plugin ID to actual Plugin Instance
    private final Map<String, LogisticsPlugin> pluginCache = new HashMap<>();

    @PostConstruct
    public void init() {
        log.info("Initializing PluginRegistryService...");
        for (LogisticsPlugin plugin : availablePlugins) {
            log.info("Discovered Plugin: {} (v{}) - {}", plugin.getPluginId(), plugin.getVersion(), plugin.getName());
            pluginCache.put(plugin.getPluginId(), plugin);
            try {
                plugin.start();
            } catch (Exception e) {
                log.error("Failed to start plugin: {}", plugin.getPluginId(), e);
            }
        }
    }

    /**
     * Retrieves all active plugins configured for a specific tenant.
     */
    public List<LogisticsPlugin> getActivePluginsForTenant(String tenantId) {
        List<TenantPluginConfig> activeConfigs = configRepository.findByTenantIdAndIsActiveTrue(tenantId);
        return activeConfigs.stream()
                .map(config -> pluginCache.get(config.getPluginId()))
                .filter(plugin -> plugin != null)
                .collect(Collectors.toList());
    }

    /**
     * Fetches a specific plugin instance if it is globally registered.
     */
    public Optional<LogisticsPlugin> getPluginById(String pluginId) {
        return Optional.ofNullable(pluginCache.get(pluginId));
    }

    /**
     * Checks if a specific plugin is enabled for a tenant.
     */
    public boolean isPluginEnabledForTenant(String tenantId, String pluginId) {
        return configRepository.findByTenantIdAndPluginId(tenantId, pluginId)
                .map(TenantPluginConfig::isActive)
                .orElse(false);
    }

    /**
     * Expose list of all discovered system-wide plugins.
     */
    public List<LogisticsPlugin> getAllDiscoveredPlugins() {
        return List.copyOf(pluginCache.values());
    }
}
