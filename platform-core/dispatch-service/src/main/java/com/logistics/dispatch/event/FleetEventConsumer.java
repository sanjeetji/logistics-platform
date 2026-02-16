package com.logistics.dispatch.event;

import com.logistics.dispatch.service.RouteOptimizationService;
import com.logistics.platform.event.dto.DriverLocationUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FleetEventConsumer {

    private final RouteOptimizationService routeOptimizationService;

    @Bean
    public Consumer<DriverLocationUpdatedEvent> driverLocationConsumer() {
        return event -> {
            log.debug("Received location update for driver: {}", event.getDriverId());
            // Periodic or threshold-based re-routing to avoid overhead
            // For MVP: simple trigger on hub boundary or frequency
            if (event.getDriverId().hashCode() % 10 == 0) { // Random sampling/periodic simulate
                routeOptimizationService.optimizeDriverRoutes(
                        "CORE_HUB",
                        event.getLatitude(),
                        event.getLongitude());
            }
        };
    }
}
