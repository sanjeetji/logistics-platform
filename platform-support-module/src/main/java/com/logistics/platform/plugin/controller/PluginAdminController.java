package com.logistics.platform.plugin.controller;

import com.logistics.platform.plugin.model.TenantPluginConfig;
import com.logistics.platform.plugin.repository.TenantPluginConfigRepository;
import com.logistics.platform.plugin.service.PluginRegistryService;
import com.logistics.platform.plugin.spi.LogisticsPlugin;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/plugins")
@RequiredArgsConstructor
public class PluginAdminController {

    private final PluginRegistryService registryService;
    private final TenantPluginConfigRepository configRepository;

    /**
     * Lists all plugins available in the current JVM classpath/Spring context.
     */
    @GetMapping("/available")
    public ResponseEntity<List<Map<String, String>>> getAvailablePlugins() {
        List<Map<String, String>> plugins = registryService.getAllDiscoveredPlugins().stream()
                .map(p -> Map.of(
                        "pluginId", p.getPluginId(),
                        "name", p.getName(),
                        "version", p.getVersion(),
                        "description", p.getDescription()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(plugins);
    }

    /**
     * Gets all configured plugins for a specific tenant.
     */
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<TenantPluginConfig>> getTenantPlugins(@PathVariable String tenantId) {
        return ResponseEntity.ok(configRepository.findByTenantIdAndIsActiveTrue(tenantId));
    }

    /**
     * Enables a specific plugin for a tenant.
     */
    @PostMapping("/tenant/{tenantId}/enable/{pluginId}")
    public ResponseEntity<TenantPluginConfig> enablePluginForTenant(
            @PathVariable String tenantId,
            @PathVariable String pluginId,
            @RequestBody(required = false) Map<String, String> payload) {

        String configJson = payload != null ? payload.getOrDefault("configJson", null) : null;

        TenantPluginConfig config = configRepository.findByTenantIdAndPluginId(tenantId, pluginId)
                .orElseGet(() -> TenantPluginConfig.builder()
                        .tenantId(tenantId)
                        .pluginId(pluginId)
                        .build());

        config.setActive(true);
        if (configJson != null) {
            config.setConfigJson(configJson);
        }

        return ResponseEntity.ok(configRepository.save(config));
    }

    /**
     * Disables a specific plugin for a tenant.
     */
    @PostMapping("/tenant/{tenantId}/disable/{pluginId}")
    public ResponseEntity<Void> disablePluginForTenant(
            @PathVariable String tenantId,
            @PathVariable String pluginId) {

        configRepository.findByTenantIdAndPluginId(tenantId, pluginId)
                .ifPresent(config -> {
                    config.setActive(false);
                    configRepository.save(config);
                });

        return ResponseEntity.ok().build();
    }
}
