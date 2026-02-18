package com.logistics.controltower.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMetric implements Serializable {
    private String metricName; // e.g., "ACTIVE_ORDERS", "DELAYED_SHIPMENTS"
    private Object value;
    private String unit;
    private LocalDateTime timestamp;
    private Map<String, Object> tags; // Region, Tenant, etc.
}
