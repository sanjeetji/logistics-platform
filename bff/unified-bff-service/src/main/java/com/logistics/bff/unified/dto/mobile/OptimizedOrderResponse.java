package com.logistics.bff.unified.dto.mobile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptimizedOrderResponse {
    // Minimal fields for mobile - reduce payload size
    private String id;
    private String status;
    private String pickup;
    private String drop;
    private Double amount;
    private String eta;
    
    // Nested objects as IDs only, fetch details on demand
    private String driverId;
    private String customerId;
    
    // Compressed location data
    private List<Double> coords; // [lat, lng] instead of full object
}
