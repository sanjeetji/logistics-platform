package com.logistics.route.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "waypoints")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
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

    @Builder.Default
    @Column(nullable = false)
    private Boolean completed = false;

    @Column(columnDefinition = "text")
    private String notes;
}
