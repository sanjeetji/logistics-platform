package com.logistics.tenant.model;

import java.util.Set;

public enum SubscriptionTier {
    FREE(5, 10, 1, false, false, Set.of(
            "REAL_TIME_TRACKING", "DRIVER_MANAGEMENT", "B2B_ORDERS", "NOTIFICATIONS_EMAIL")),

    BRONZE(1000, 100, 5, true, false, Set.of(
            "REAL_TIME_TRACKING", "DRIVER_MANAGEMENT", "B2B_ORDERS", "NOTIFICATIONS_EMAIL",
            "MULTI_VEHICLE_SUPPORT", "BULK_ORDER_UPLOAD", "ANALYTICS_DASHBOARD")),

    SILVER(5000, 500, 20, true, true, Set.of(
            "REAL_TIME_TRACKING", "DRIVER_MANAGEMENT", "B2B_ORDERS", "B2C_ORDERS", "NOTIFICATIONS_EMAIL",
            "NOTIFICATIONS_SMS", "MULTI_VEHICLE_SUPPORT", "BULK_ORDER_UPLOAD", "ANALYTICS_DASHBOARD",
            "API_ACCESS", "WEBHOOK_SUPPORT", "ROUTE_OPTIMIZATION", "DYNAMIC_PRICING", // Next-Gen Mappings
            "MULTI_CARRIER_NETWORK", "SECURE_EPOD", "CARBON_EMISSION_TRACKING",
            "DRIVER_BEHAVIOR_TRACKING", "ADVANCED_DRIVER_ANALYTICS")),

    GOLD(20000, -1, -1, true, true, Set.of(// Everything above, plus Gold-exclusive Next-Gen features
            "REAL_TIME_TRACKING", "DRIVER_MANAGEMENT", "B2B_ORDERS", "B2C_ORDERS", "NOTIFICATIONS_EMAIL",
            "NOTIFICATIONS_SMS", "NOTIFICATIONS_PUSH", "MULTI_VEHICLE_SUPPORT", "BULK_ORDER_UPLOAD",
            "ANALYTICS_DASHBOARD", "EXPORT_REPORTS", "API_ACCESS", "WEBHOOK_SUPPORT", "PAYROLL_INTEGRATION",
            "ROUTE_OPTIMIZATION", "DYNAMIC_PRICING", "SURGE_PRICING", "CUSTOM_SLA", "WAREHOUSE_MANAGEMENT",
            "ORCHESTRATION_SAGA", "AI_DEMAND_FORECAST", // Next-Gen Mappings
            "CROSS_BORDER_COMPLIANCE", "FREIGHT_PROCUREMENT", "CARBON_EMISSION_TRACKING",
            "PREDICTIVE_MAINTENANCE", "CONTROL_TOWER_DASHBOARD", "SMART_WAREHOUSING",
            "MULTI_CARRIER_NETWORK", "ADVANCED_ROUTING_ENGINE", "SECURE_EPOD", "DIGITAL_TWIN_SIMULATION",
            "DRIVER_BEHAVIOR_TRACKING", "ADVANCED_DRIVER_ANALYTICS"));

    private final int monthlyOrderLimit;
    private final int maxDrivers;
    private final int maxVehicles;
    private final boolean analyticsEnabled;
    private final boolean apiAccessEnabled;
    private final Set<String> defaultFeatures;

    SubscriptionTier(int monthlyOrderLimit, int maxDrivers, int maxVehicles,
            boolean analyticsEnabled, boolean apiAccessEnabled, Set<String> defaultFeatures) {
        this.monthlyOrderLimit = monthlyOrderLimit;
        this.maxDrivers = maxDrivers;
        this.maxVehicles = maxVehicles;
        this.analyticsEnabled = analyticsEnabled;
        this.apiAccessEnabled = apiAccessEnabled;
        this.defaultFeatures = defaultFeatures;
    }

    public int getMonthlyOrderLimit() {
        return monthlyOrderLimit;
    }

    public int getMaxDrivers() {
        return maxDrivers;
    }

    public int getMaxVehicles() {
        return maxVehicles;
    }

    public boolean isAnalyticsEnabled() {
        return analyticsEnabled;
    }

    public boolean isApiAccessEnabled() {
        return apiAccessEnabled;
    }

    public Set<String> getDefaultFeatures() {
        return defaultFeatures;
    }

    public boolean isUnlimited(String resource) {
        return switch (resource.toLowerCase()) {
            case "drivers" -> maxDrivers == -1;
            case "vehicles" -> maxVehicles == -1;
            case "orders" -> monthlyOrderLimit == -1;
            default -> false;
        };
    }
}
