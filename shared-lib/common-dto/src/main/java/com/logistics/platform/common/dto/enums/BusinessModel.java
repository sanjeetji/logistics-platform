package com.logistics.platform.common.dto.enums;

/**
 * Defines the core business model of a Tenant.
 * Used to drive system-wide fulfillment strategies, pricing, and entity
 * behavior.
 */
public enum BusinessModel {
    /**
     * Business-to-Business (Enterprise).
     * Focuses on bulk shipments, scheduled routes, contract pricing, and commercial
     * fleets.
     */
    B2B,

    /**
     * Business-to-Consumer (Retail).
     * Focuses on on-demand delivery, instant pay, dynamic surge, and gig-economy
     * riders.
     */
    B2C,

    /**
     * Hybrid/Marketplace model.
     * Supports both enterprise fulfillment and retail consumer services.
     */
    HYBRID
}
