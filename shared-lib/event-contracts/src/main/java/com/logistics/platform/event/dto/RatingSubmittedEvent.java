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
public class RatingSubmittedEvent extends BaseEvent {

    private String orderId;
    private String userId;
    private String targetId;
    private String targetType; // DRIVER or SERVICE
    private Integer score;
    private String reviewText;

    public static RatingSubmittedEvent create(
            String orderId,
            String userId,
            String targetId,
            String targetType,
            Integer score,
            String reviewText) {
        return RatingSubmittedEvent.builder()
                .orderId(orderId)
                .userId(userId)
                .targetId(targetId)
                .targetType(targetType)
                .score(score)
                .reviewText(reviewText)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
