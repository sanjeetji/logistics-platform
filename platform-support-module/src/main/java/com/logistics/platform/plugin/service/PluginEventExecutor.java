package com.logistics.platform.plugin.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.platform.plugin.spi.LogisticsPlugin;
import com.logistics.platform.plugin.spi.OrderInterceptorPlugin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service that listens to generic platform events (like Order statuses)
 * and dispatches them to any active plugins registered for the payload's
 * tenant.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PluginEventExecutor {

    private final PluginRegistryService pluginRegistryService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-events", groupId = "plugin-executor-group")
    public void handleOrderEvent(String message) {
        try {
            Map<String, Object> eventPayload = objectMapper.readValue(message,
                    new TypeReference<Map<String, Object>>() {
                    });

            // Extract standard metadata expected across all our platform events
            String tenantId = (String) eventPayload.get("tenantId");
            String orderId = (String) eventPayload.get("orderId");
            String eventType = (String) eventPayload.get("eventType");

            if (tenantId == null || orderId == null) {
                log.debug("Skipping plugin execution for un-parseable or tenant-less order event.");
                return;
            }

            // Look up active plugins for this specific tenant
            List<LogisticsPlugin> activePlugins = pluginRegistryService.getActivePluginsForTenant(tenantId);

            if (activePlugins.isEmpty()) {
                return; // No plugins to execute for this tenant
            }

            log.debug("Dispatching event {} for Order {} to {} active plugins for Tenant {}",
                    eventType, orderId, activePlugins.size(), tenantId);

            for (LogisticsPlugin plugin : activePlugins) {
                if (plugin instanceof OrderInterceptorPlugin) {
                    try {
                        ((OrderInterceptorPlugin) plugin).processOrderEvent(tenantId, orderId, eventType, eventPayload);
                    } catch (Exception e) {
                        // Log but swallow. A badly written tenant plugin should NOT crash the generic
                        // listener pool
                        log.error("Plugin {} threw exception processing event {} for Order {}",
                                plugin.getPluginId(), eventType, orderId, e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to parse or route generic event to plugins", e);
        }
    }
}
