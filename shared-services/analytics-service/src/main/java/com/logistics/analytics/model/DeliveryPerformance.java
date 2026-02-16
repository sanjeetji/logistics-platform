package com.logistics.analytics.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_performance")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPerformance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;
    private Long driverId;
    private String routeId;

    private LocalDateTime promisedTime;
    private LocalDateTime actualTime;

    private Long predictedDurationSeconds;
    private Long actualDurationSeconds;

    // For ML training
    private Double pickupLat;
    private Double pickupLng;
    private Double dropLat;
    private Double dropLng;
    private String timeOfDay; // Morning, Afternoon, Evening
    private String dayOfWeek;
    private String weatherCondition; // If available
}
