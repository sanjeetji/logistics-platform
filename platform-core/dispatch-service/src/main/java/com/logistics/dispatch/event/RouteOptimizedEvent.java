package com.logistics.dispatch.event;

import com.logistics.dispatch.client.MLServiceClient;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class RouteOptimizedEvent extends ApplicationEvent {
    private final List<MLServiceClient.VehicleRoute> routes;

    public RouteOptimizedEvent(Object source, List<MLServiceClient.VehicleRoute> routes) {
        super(source);
        this.routes = routes;
    }
}
