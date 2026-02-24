package com.logistics.tenant.config;

import com.logistics.tenant.model.FeatureFlag;
import com.logistics.tenant.repository.FeatureFlagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Seeds the master feature flag list into the database on application startup.
 * Skips any feature that already exists (idempotent).
 * SUPER_ADMIN can then toggle these per tenant via the control panel.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FeatureDataSeeder implements ApplicationRunner {

    private final FeatureFlagRepository featureFlagRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedFeatures();
    }

    private void seedFeatures() {
        List<FeatureSeed> seeds = List.of(

                // ── FLEET ────────────────────────────────────────────────────────
                new FeatureSeed("REAL_TIME_TRACKING", "Real-Time GPS Tracking", "FLEET",
                        "Live GPS tracking of drivers and shipments", true),
                new FeatureSeed("ROUTE_OPTIMIZATION", "Route Optimization", "FLEET",
                        "AI-based route optimization to reduce delivery time & cost", false),
                new FeatureSeed("MULTI_VEHICLE_SUPPORT", "Multi-Vehicle Management", "FLEET",
                        "Manage fleets with multiple vehicle types", false),
                new FeatureSeed("DRIVER_MANAGEMENT", "Driver Management", "FLEET",
                        "Manage driver profiles, availability, and performance", true),

                // ── ORDERS ───────────────────────────────────────────────────────
                new FeatureSeed("B2B_ORDERS", "B2B Order Management", "ORDERS",
                        "Business-to-business order creation and fulfillment", true),
                new FeatureSeed("B2C_ORDERS", "B2C Order Management", "ORDERS",
                        "Consumer-facing order creation (Porter-style)", false),
                new FeatureSeed("BULK_ORDER_UPLOAD", "Bulk Order Upload", "ORDERS",
                        "Upload large batches of orders via CSV file", false),
                new FeatureSeed("CUSTOM_SLA", "Custom SLA Rules", "ORDERS",
                        "Define custom Service Level Agreements per client", false),

                // ── WAREHOUSE ────────────────────────────────────────────────────
                new FeatureSeed("WAREHOUSE_MANAGEMENT", "Warehouse Management", "WAREHOUSE",
                        "Manage warehouse operations, inventory, and stock", false),

                // ── PRICING ──────────────────────────────────────────────────────
                new FeatureSeed("DYNAMIC_PRICING", "Dynamic Pricing Engine", "PRICING",
                        "Automatically adjust prices based on demand and distance", false),
                new FeatureSeed("SURGE_PRICING", "Surge Pricing", "PRICING",
                        "Apply surge multipliers during peak hours", false),

                // ── ANALYTICS ────────────────────────────────────────────────────
                new FeatureSeed("ANALYTICS_DASHBOARD", "Analytics Dashboard", "ANALYTICS",
                        "Visualize KPIs, order trends, and driver performance", false),
                new FeatureSeed("EXPORT_REPORTS", "Export Reports", "ANALYTICS",
                        "Export analytics data as PDF or CSV", false),

                // ── NOTIFICATIONS ─────────────────────────────────────────────────
                new FeatureSeed("NOTIFICATIONS_EMAIL", "Email Notifications", "NOTIFICATIONS",
                        "Send order updates and alerts via email", true),
                new FeatureSeed("NOTIFICATIONS_SMS", "SMS Notifications", "NOTIFICATIONS",
                        "Send order updates and alerts via SMS", false),
                new FeatureSeed("NOTIFICATIONS_PUSH", "Push Notifications", "NOTIFICATIONS",
                        "Send real-time push notifications to mobile apps", false),

                // ── PLATFORM & INTEGRATION ────────────────────────────────────────
                new FeatureSeed("API_ACCESS", "External API Access", "PLATFORM",
                        "Allow external systems to integrate via REST API keys", false),
                new FeatureSeed("WEBHOOK_SUPPORT", "Webhook Subscriptions", "PLATFORM",
                        "Subscribe to platform events via HTTP webhooks", false),
                new FeatureSeed("PAYROLL_INTEGRATION", "Payroll Integration", "PLATFORM",
                        "Integrate with payroll systems for driver payments", false),

                // ── AI / ADVANCED ─────────────────────────────────────────────────
                new FeatureSeed("ORCHESTRATION_SAGA", "Saga Orchestration Engine", "AI",
                        "Use the saga pattern for distributed order orchestration", false),
                new FeatureSeed("AI_DEMAND_FORECAST", "AI Demand Forecasting", "AI",
                        "Predict future order volumes using machine learning", false));

        int seeded = 0;
        for (FeatureSeed seed : seeds) {
            if (!featureFlagRepository.existsByFeatureKey(seed.key())) {
                @SuppressWarnings("null")
                var saved = featureFlagRepository.save(
                        FeatureFlag.builder()
                                .featureKey(seed.key())
                                .featureName(seed.name())
                                .category(seed.category())
                                .description(seed.description())
                                .globallyEnabled(seed.globallyEnabled())
                                .build());
                log.debug("Seeded feature: {}", saved.getFeatureKey());
                seeded++;
            }
        }

        if (seeded > 0) {
            log.info("Feature Control Panel: seeded {} new feature flags into the database", seeded);
        } else {
            log.info("Feature Control Panel: all {} feature flags already exist — skipping seed", seeds.size());
        }
    }

    /** Simple record to hold seed data */
    private record FeatureSeed(
            String key,
            String name,
            String category,
            String description,
            boolean globallyEnabled) {
    }
}
