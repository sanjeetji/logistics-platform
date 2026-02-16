package com.logistics.routing.service;

import com.logistics.routing.algorithm.RoutingAlgorithm;
import com.logistics.routing.dto.RouteOptimizationRequest;
import com.logistics.routing.dto.RouteOptimizationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteOptimizationService {

    private final List<RoutingAlgorithm> algorithms;
    private final DistanceMatrixService distanceMatrixService; // Keep if needed for score calculation or remove

    /**
     * Optimize route using selected strategy
     */
    public RouteOptimizationResponse optimizeRoute(RouteOptimizationRequest request) {
        RoutingAlgorithm algorithm = algorithms.stream()
                .filter(a -> a.getType() == request.getOptimizationType())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No algorithm found for type: " + request.getOptimizationType()));

        return algorithm.optimize(request);
    }

    /**
     * Calculate optimization score (0-100)
     * Kept for backward compatibility or utility
     */
    public double calculateOptimizationScore(double optimizedDistance, double naiveDistance) {
        if (naiveDistance == 0)
            return 100.0;
        double improvement = ((naiveDistance - optimizedDistance) / naiveDistance) * 100;
        return Math.max(0, Math.min(100, improvement));
    }
}
