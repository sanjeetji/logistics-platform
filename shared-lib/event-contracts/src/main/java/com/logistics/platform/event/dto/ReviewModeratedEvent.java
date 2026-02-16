package com.logistics.platform.event.dto;

import com.logistics.platform.event.dto.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ReviewModeratedEvent extends BaseEvent {

    private Long ratingId;
    private String moderationStatus; // APPROVED, REJECTED, AUTO_APPROVED
    private String moderatedBy;
    private LocalDateTime moderatedAt;
    private String moderationReason;

    public static ReviewModeratedEvent create(
            Long ratingId,
            String moderationStatus,
            String moderatedBy,
            LocalDateTime moderatedAt,
            String moderationReason) {
        return ReviewModeratedEvent.builder()
                .ratingId(ratingId)
                .moderationStatus(moderationStatus)
                .moderatedBy(moderatedBy)
                .moderatedAt(moderatedAt)
                .moderationReason(moderationReason)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
