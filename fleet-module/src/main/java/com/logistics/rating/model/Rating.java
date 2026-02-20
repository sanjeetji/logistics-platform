package com.logistics.rating.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ratings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rating extends BaseEntity {

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String userId; // User who is rating

    @Column(nullable = false)
    private String targetId; // Driver ID or Service ID being rated

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RatingTargetType targetType;

    @Column(nullable = false)
    private Integer score; // 1-5

    @Column(columnDefinition = "text")
    private String reviewText;

    // Moderation fields
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ModerationStatus moderationStatus = ModerationStatus.PENDING;

    private String moderationReason; // Reason for rejection

    private LocalDateTime moderatedAt;
    private String moderatedBy;

    // Feedback categories
    @ElementCollection
    @CollectionTable(name = "rating_feedback_categories", joinColumns = @JoinColumn(name = "rating_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    @Builder.Default
    private List<FeedbackCategory> feedbackCategories = new ArrayList<>();

    public enum RatingTargetType {
        DRIVER, SERVICE
    }

    public enum ModerationStatus {
        PENDING, APPROVED, REJECTED, AUTO_APPROVED
    }

    public enum FeedbackCategory {
        PROFESSIONALISM,
        TIMELINESS,
        COMMUNICATION,
        VEHICLE_CONDITION,
        PACKAGE_HANDLING,
        OVERALL_EXPERIENCE
    }
}
