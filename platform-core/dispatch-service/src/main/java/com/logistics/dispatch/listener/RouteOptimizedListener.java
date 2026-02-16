package com.logistics.dispatch.listener;

import com.logistics.dispatch.event.DispatchEventProducer;
import com.logistics.dispatch.event.RouteOptimizedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RouteOptimizedListener {

    private final DispatchEventProducer eventProducer;

    @EventListener
    public void handleRouteOptimized(RouteOptimizedEvent event) {
        log.info("Handling RouteOptimizedEvent with {} routes", event.getRoutes().size());

        event.getRoutes().forEach(route -> {
            if (!route.getRouteOrderIds().isEmpty()) {
                eventProducer.publishRouteUpdate(
                        route.getVehicleId(),
                        route.getRouteOrderIds());
            }
        });
    }
}
