package com.logistics.order.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_stops", indexes = {
        @Index(name = "idx_stop_order", columnList = "order_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStop extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private Integer stopSequence;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StopType stopType;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "address", column = @Column(name = "address")),
            @AttributeOverride(name = "latitude", column = @Column(name = "latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "longitude")),
            @AttributeOverride(name = "contactName", column = @Column(name = "contact_name")),
            @AttributeOverride(name = "contactPhone", column = @Column(name = "contact_phone")),
            @AttributeOverride(name = "instructions", column = @Column(name = "instructions"))
    })
    private OrderLocation location;

    private LocalDateTime estimatedArrival;

    private LocalDateTime actualArrival;

    @Column(columnDefinition = "text")
    private String items; // JSON or comma-separated list related to this stop

    @Column(columnDefinition = "text")
    private String notes;

    @Builder.Default
    @Column(nullable = false)
    private Boolean completed = false;
}
