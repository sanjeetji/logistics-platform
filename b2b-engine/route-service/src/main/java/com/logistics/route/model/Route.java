package com.logistics.route.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "routes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
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
    @Builder.Default
    private List<Waypoint> waypoints = new ArrayList<>();

    @Column(nullable = false)
    private Double totalDistance; // in kilometers

    @Column(nullable = false)
    private Integer estimatedDuration; // in minutes

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private RouteStatus status = RouteStatus.PLANNED;

    private Double optimizationScore; // Efficiency metric (0-100)

    @Column(columnDefinition = "text")
    private String notes;

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
