package com.logistics.loyalty.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "points_transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointsTransaction extends BaseEntity {

    @Column(nullable = false)
    private Long profileId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false)
    private Integer points;

    private String referenceId; // Order ID or Redemption ID

    private String description;

    public enum TransactionType {
        EARN,
        REDEEM,
        ADJUSTMENT
    }
}
