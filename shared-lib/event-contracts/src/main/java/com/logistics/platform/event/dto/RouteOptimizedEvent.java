package com.logistics.platform.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RouteOptimizedEvent extends BaseEvent {
    private List<VehicleRouteDto> routes;

    public static RouteOptimizedEvent create(List<VehicleRouteDto> routes) {
        return RouteOptimizedEvent.builder()
                .routes(routes)
                .eventType("ROUTE_OPTIMIZED")
                .build();
    }
}
