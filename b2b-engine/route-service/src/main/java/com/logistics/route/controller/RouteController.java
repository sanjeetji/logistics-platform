package com.logistics.route.controller;

import com.logistics.route.dto.RouteOptimizationRequest;
import com.logistics.route.model.Route;
import com.logistics.route.service.RoutePlanningService;
import com.logistics.platform.common.dto.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RoutePlanningService routePlanningService;

    @PostMapping("/optimize")
    public ResponseEntity<ApiResponse<List<Route>>> optimizeRoutes(
            @Valid @RequestBody RouteOptimizationRequest request) {
        List<Route> routes = routePlanningService.createOptimizedRoutes(request);
        return ResponseEntity.ok(ApiResponse.success(routes, "Routes optimized successfully"));
    }

    @GetMapping("/{routeId}")
    public ResponseEntity<ApiResponse<Route>> getRoute(@PathVariable String routeId) {
        Route route = routePlanningService.getRouteById(routeId);
        return ResponseEntity.ok(ApiResponse.success(route));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<ApiResponse<List<Route>>> getDriverRoutes(@PathVariable Long driverId) {
        List<Route> routes = routePlanningService.getDriverRoutes(driverId);
        return ResponseEntity.ok(ApiResponse.success(routes));
    }

    @PutMapping("/{routeId}/reoptimize")
    public ResponseEntity<ApiResponse<Route>> reoptimizeRoute(@PathVariable String routeId) {
        Route route = routePlanningService.reoptimizeRoute(routeId);
        return ResponseEntity.ok(ApiResponse.success(route, "Route re-optimized"));
    }
}
