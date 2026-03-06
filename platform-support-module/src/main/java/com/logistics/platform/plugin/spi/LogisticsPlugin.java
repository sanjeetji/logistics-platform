package com.logistics.platform.plugin.spi;

/**
 * Base Service Provider Interface (SPI) for all dynamic logistics plugins.
 * Implementations of this interface can be dynamically loaded and assigned
 * to specific tenants to override or augment core processing behaviors.
 */
public interface LogisticsPlugin {

    /**
     * Unique string identifier for the plugin (e.g., "high-value-audit-plugin").
     */
    String getPluginId();

    /**
     * The semantic version of the plugin.
     */
    default String getVersion() {
        return "1.0.0";
    }

    /**
     * Name of the plugin.
     */
    String getName();

    /**
     * Description of what this plugin does.
     */
    String getDescription();

    /**
     * Called when the plugin is activated for the first time.
     */
    default void start() {
    }

    /**
     * Called when the plugin is deactivated or system shuts down.
     */
    default void stop() {
    }
}
