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
    private String id;
    private String status;
    private String pickup;
    private String drop;
    private Double amount;
    private String eta;
    private String driverId;
    private String customerId;
    private List<Double> coords;
}
