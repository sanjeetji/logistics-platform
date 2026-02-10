package com.logistics.route.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "routes")
public class Route extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String routeId;

    @Column(nullable = false)
    private Long vehicleId;

    private Long driverId;

    @Column(nullable = false)
    private LocalDate routeDate;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("waypointSequence ASC")
    private List<Waypoint> waypoints = new ArrayList<>();

    @Column(nullable = false)
    private Double totalDistance; // in kilometers

    @Column(nullable = false)
    private Integer estimatedDuration; // in minutes

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RouteStatus status = RouteStatus.PLANNED;

    private Double optimizationScore; // Efficiency metric (0-100)

    @Column(columnDefinition = "text")
    private String notes;

    public Route() {}

    public Route(String routeId, Long vehicleId, Long driverId, LocalDate routeDate, List<Waypoint> waypoints, Double totalDistance, Integer estimatedDuration, RouteStatus status, Double optimizationScore, String notes) {
        this.routeId = routeId;
        this.vehicleId = vehicleId;
        this.driverId = driverId;
        this.routeDate = routeDate;
        this.waypoints = waypoints != null ? waypoints : new ArrayList<>();
        this.totalDistance = totalDistance;
        this.estimatedDuration = estimatedDuration;
        this.status = status != null ? status : RouteStatus.PLANNED;
        this.optimizationScore = optimizationScore;
        this.notes = notes;
    }

    public static RouteBuilder builder() {
        return new RouteBuilder();
    }

    public String getRouteId() { return routeId; }
    public void setRouteId(String routeId) { this.routeId = routeId; }

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }

    public LocalDate getRouteDate() { return routeDate; }
    public void setRouteDate(LocalDate routeDate) { this.routeDate = routeDate; }

    public List<Waypoint> getWaypoints() { return waypoints; }
    public void setWaypoints(List<Waypoint> waypoints) { this.waypoints = waypoints; }

    public Double getTotalDistance() { return totalDistance; }
    public void setTotalDistance(Double totalDistance) { this.totalDistance = totalDistance; }

    public Integer getEstimatedDuration() { return estimatedDuration; }
    public void setEstimatedDuration(Integer estimatedDuration) { this.estimatedDuration = estimatedDuration; }

    public RouteStatus getStatus() { return status; }
    public void setStatus(RouteStatus status) { this.status = status; }

    public Double getOptimizationScore() { return optimizationScore; }
    public void setOptimizationScore(Double optimizationScore) { this.optimizationScore = optimizationScore; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public static class RouteBuilder {
        private String routeId;
        private Long vehicleId;
        private Long driverId;
        private LocalDate routeDate;
        private List<Waypoint> waypoints = new ArrayList<>();
        private Double totalDistance;
        private Integer estimatedDuration;
        private RouteStatus status = RouteStatus.PLANNED;
        private Double optimizationScore;
        private String notes;

        public RouteBuilder routeId(String routeId) { this.routeId = routeId; return this; }
        public RouteBuilder vehicleId(Long vehicleId) { this.vehicleId = vehicleId; return this; }
        public RouteBuilder driverId(Long driverId) { this.driverId = driverId; return this; }
        public RouteBuilder routeDate(LocalDate routeDate) { this.routeDate = routeDate; return this; }
        public RouteBuilder waypoints(List<Waypoint> waypoints) { this.waypoints = waypoints; return this; }
        public RouteBuilder totalDistance(Double totalDistance) { this.totalDistance = totalDistance; return this; }
        public RouteBuilder estimatedDuration(Integer estimatedDuration) { this.estimatedDuration = estimatedDuration; return this; }
        public RouteBuilder status(RouteStatus status) { this.status = status; return this; }
        public RouteBuilder optimizationScore(Double optimizationScore) { this.optimizationScore = optimizationScore; return this; }
        public RouteBuilder notes(String notes) { this.notes = notes; return this; }

        public Route build() {
            return new Route(routeId, vehicleId, driverId, routeDate, waypoints, totalDistance, estimatedDuration, status, optimizationScore, notes);
        }
    }

    // Helper method to add waypoint
    public void addWaypoint(Waypoint waypoint) {
        waypoints.add(waypoint);
        waypoint.setRoute(this);
    }

    // Calculate total distance
    public void calculateTotalDistance() {
        this.totalDistance = waypoints.stream()
                .mapToDouble(w -> w.getDistanceFromPrevious() != null ? w.getDistanceFromPrevious() : 0.0)
                .sum();
    }

    // Calculate estimated duration
    public void calculateEstimatedDuration() {
        // Assume average speed of 30 km/h + service time at each stop
        double travelTime = (totalDistance / 30.0) * 60; // minutes
        int serviceTime = waypoints.size() * 10; // 10 min per stop
        this.estimatedDuration = (int) (travelTime + serviceTime);
    }
}
