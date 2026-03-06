package com.logistics.loyalty.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "loyalty_profiles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyProfile extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String userId;

    @Builder.Default
    @Column(nullable = false)
    private Integer currentPoints = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer totalPointsEarned = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Tier currentTier = Tier.BRONZE;

    private LocalDateTime lastActivityDate;

    public enum Tier {
        BRONZE,
        SILVER,
        GOLD,
        PLATINUM
    }
}
