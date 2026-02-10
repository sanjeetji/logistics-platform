package com.logistics.route.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class RouteOptimizationRequest {
    
    @NotEmpty(message = "Order IDs are required")
    private List<String> orderIds;
    
    @NotEmpty(message = "Vehicle IDs are required")
    private List<Long> vehicleIds;
    
    @NotNull(message = "Route date is required")
    private LocalDate routeDate;
    
    private LocationDTO startLocation; // Depot/warehouse
    
    private LocationDTO endLocation; // Return location
    
    private Map<String, Object> constraints; // Time windows, capacity, etc.

    public RouteOptimizationRequest() {}

    public RouteOptimizationRequest(List<String> orderIds, List<Long> vehicleIds, LocalDate routeDate, LocationDTO startLocation, LocationDTO endLocation, Map<String, Object> constraints) {
        this.orderIds = orderIds;
        this.vehicleIds = vehicleIds;
        this.routeDate = routeDate;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.constraints = constraints;
    }

    public static RouteOptimizationRequestBuilder builder() {
        return new RouteOptimizationRequestBuilder();
    }

    public List<String> getOrderIds() { return orderIds; }
    public void setOrderIds(List<String> orderIds) { this.orderIds = orderIds; }

    public List<Long> getVehicleIds() { return vehicleIds; }
    public void setVehicleIds(List<Long> vehicleIds) { this.vehicleIds = vehicleIds; }

    public LocalDate getRouteDate() { return routeDate; }
    public void setRouteDate(LocalDate routeDate) { this.routeDate = routeDate; }

    public LocationDTO getStartLocation() { return startLocation; }
    public void setStartLocation(LocationDTO startLocation) { this.startLocation = startLocation; }

    public LocationDTO getEndLocation() { return endLocation; }
    public void setEndLocation(LocationDTO endLocation) { this.endLocation = endLocation; }

    public Map<String, Object> getConstraints() { return constraints; }
    public void setConstraints(Map<String, Object> constraints) { this.constraints = constraints; }

    public static class RouteOptimizationRequestBuilder {
        private List<String> orderIds;
        private List<Long> vehicleIds;
        private LocalDate routeDate;
        private LocationDTO startLocation;
        private LocationDTO endLocation;
        private Map<String, Object> constraints;

        public RouteOptimizationRequestBuilder orderIds(List<String> orderIds) { this.orderIds = orderIds; return this; }
        public RouteOptimizationRequestBuilder vehicleIds(List<Long> vehicleIds) { this.vehicleIds = vehicleIds; return this; }
        public RouteOptimizationRequestBuilder routeDate(LocalDate routeDate) { this.routeDate = routeDate; return this; }
        public RouteOptimizationRequestBuilder startLocation(LocationDTO startLocation) { this.startLocation = startLocation; return this; }
        public RouteOptimizationRequestBuilder endLocation(LocationDTO endLocation) { this.endLocation = endLocation; return this; }
        public RouteOptimizationRequestBuilder constraints(Map<String, Object> constraints) { this.constraints = constraints; return this; }

        public RouteOptimizationRequest build() {
            return new RouteOptimizationRequest(orderIds, vehicleIds, routeDate, startLocation, endLocation, constraints);
        }
    }
}
