package com.logistics.route.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "waypoints")
public class Waypoint extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(nullable = false)
    private Integer waypointSequence;

    private String orderId; // Reference to B2B order

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WaypointType waypointType;

    @Column(nullable = false, columnDefinition = "text")
    private String address;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private LocalDateTime estimatedArrival;

    private LocalDateTime actualArrival;

    private Integer serviceTime; // in minutes

    private Double distanceFromPrevious; // in kilometers

    @Column(nullable = false)
    private Boolean completed = false;

    @Column(columnDefinition = "text")
    private String notes;

    public Waypoint() {}

    public Waypoint(Route route, Integer waypointSequence, String orderId, WaypointType waypointType, String address, Double latitude, Double longitude, LocalDateTime estimatedArrival, LocalDateTime actualArrival, Integer serviceTime, Double distanceFromPrevious, Boolean completed, String notes) {
        this.route = route;
        this.waypointSequence = waypointSequence;
        this.orderId = orderId;
        this.waypointType = waypointType;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.estimatedArrival = estimatedArrival;
        this.actualArrival = actualArrival;
        this.serviceTime = serviceTime;
        this.distanceFromPrevious = distanceFromPrevious;
        this.completed = completed != null ? completed : false;
        this.notes = notes;
    }

    public static WaypointBuilder builder() {
        return new WaypointBuilder();
    }

    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }

    public Integer getWaypointSequence() { return waypointSequence; }
    public void setWaypointSequence(Integer waypointSequence) { this.waypointSequence = waypointSequence; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public WaypointType getWaypointType() { return waypointType; }
    public void setWaypointType(WaypointType waypointType) { this.waypointType = waypointType; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public LocalDateTime getEstimatedArrival() { return estimatedArrival; }
    public void setEstimatedArrival(LocalDateTime estimatedArrival) { this.estimatedArrival = estimatedArrival; }

    public LocalDateTime getActualArrival() { return actualArrival; }
    public void setActualArrival(LocalDateTime actualArrival) { this.actualArrival = actualArrival; }

    public Integer getServiceTime() { return serviceTime; }
    public void setServiceTime(Integer serviceTime) { this.serviceTime = serviceTime; }

    public Double getDistanceFromPrevious() { return distanceFromPrevious; }
    public void setDistanceFromPrevious(Double distanceFromPrevious) { this.distanceFromPrevious = distanceFromPrevious; }

    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public static class WaypointBuilder {
        private Route route;
        private Integer waypointSequence;
        private String orderId;
        private WaypointType waypointType;
        private String address;
        private Double latitude;
        private Double longitude;
        private LocalDateTime estimatedArrival;
        private LocalDateTime actualArrival;
        private Integer serviceTime;
        private Double distanceFromPrevious;
        private Boolean completed = false;
        private String notes;

        public WaypointBuilder route(Route route) { this.route = route; return this; }
        public WaypointBuilder waypointSequence(Integer waypointSequence) { this.waypointSequence = waypointSequence; return this; }
        public WaypointBuilder orderId(String orderId) { this.orderId = orderId; return this; }
        public WaypointBuilder waypointType(WaypointType waypointType) { this.waypointType = waypointType; return this; }
        public WaypointBuilder address(String address) { this.address = address; return this; }
        public WaypointBuilder latitude(Double latitude) { this.latitude = latitude; return this; }
        public WaypointBuilder longitude(Double longitude) { this.longitude = longitude; return this; }
        public WaypointBuilder estimatedArrival(LocalDateTime estimatedArrival) { this.estimatedArrival = estimatedArrival; return this; }
        public WaypointBuilder actualArrival(LocalDateTime actualArrival) { this.actualArrival = actualArrival; return this; }
        public WaypointBuilder serviceTime(Integer serviceTime) { this.serviceTime = serviceTime; return this; }
        public WaypointBuilder distanceFromPrevious(Double distanceFromPrevious) { this.distanceFromPrevious = distanceFromPrevious; return this; }
        public WaypointBuilder completed(Boolean completed) { this.completed = completed; return this; }
        public WaypointBuilder notes(String notes) { this.notes = notes; return this; }

        public Waypoint build() {
            return new Waypoint(route, waypointSequence, orderId, waypointType, address, latitude, longitude, estimatedArrival, actualArrival, serviceTime, distanceFromPrevious, completed, notes);
        }
    }
}
