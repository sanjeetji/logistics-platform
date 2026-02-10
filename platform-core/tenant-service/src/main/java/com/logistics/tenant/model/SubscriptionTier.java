package com.logistics.tenant.model;

public enum SubscriptionTier {
    FREE(0, 10, 1, false, false),
    BRONZE(1000, 100, 5, true, false),
    SILVER(5000, 500, 20, true, true),
    GOLD(20000, -1, -1, true, true); // -1 means unlimited

    private final int monthlyOrderLimit;
    private final int maxDrivers;
    private final int maxVehicles;
    private final boolean analyticsEnabled;
    private final boolean apiAccessEnabled;

    SubscriptionTier(int monthlyOrderLimit, int maxDrivers, int maxVehicles,
            boolean analyticsEnabled, boolean apiAccessEnabled) {
        this.monthlyOrderLimit = monthlyOrderLimit;
        this.maxDrivers = maxDrivers;
        this.maxVehicles = maxVehicles;
        this.analyticsEnabled = analyticsEnabled;
        this.apiAccessEnabled = apiAccessEnabled;
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

    public boolean isUnlimited(String resource) {
        return switch (resource.toLowerCase()) {
            case "drivers" -> maxDrivers == -1;
            case "vehicles" -> maxVehicles == -1;
            case "orders" -> monthlyOrderLimit == -1;
            default -> false;
        };
    }
}
