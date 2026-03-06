package com.logistics.returns.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "return_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String returnId;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReturnReason reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReturnStatus status;

    private String description;

    // For RTO (Return to Origin) Logistics
    private String pickupAddress;
    private Double pickupLatitude;
    private Double pickupLongitude;

    private BigDecimal refundAmount;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "return_proof_images", joinColumns = @JoinColumn(name = "return_id"))
    @Column(name = "image_url")
    private List<String> proofImages = new ArrayList<>();

    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;

    private String adminNotes;

    @PrePersist
    public void onCreate() {
        this.requestedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ReturnStatus.REQUESTED;
        }
    }
}
