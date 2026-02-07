package com.logistics.b2b.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_stops")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderStop extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private B2BOrder order;

    @Column(nullable = false)
    private Integer stopSequence;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StopType stopType;

    @Column(nullable = false, columnDefinition = "text")
    private String address;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private String contactName;
    
    private String contactPhone;

    private LocalDateTime estimatedArrival;
    
    private LocalDateTime actualArrival;

    @Column(columnDefinition = "text")
    private String items; // JSON or comma-separated list

    @Column(columnDefinition = "text")
    private String notes;

    @Builder.Default
    @Column(nullable = false)
    private Boolean completed = false;
}
