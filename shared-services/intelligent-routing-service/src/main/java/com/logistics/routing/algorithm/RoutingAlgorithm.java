package com.logistics.routing.algorithm;

import com.logistics.routing.dto.RouteOptimizationRequest;
import com.logistics.routing.dto.RouteOptimizationResponse;

public interface RoutingAlgorithm {
    RouteOptimizationResponse optimize(RouteOptimizationRequest request);

    RouteOptimizationRequest.OptimizationType getType();
}
