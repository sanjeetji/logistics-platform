package com.logistics.rating.dto;

import com.logistics.rating.model.Rating;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitRatingRequest {

    @NotNull(message = "Order ID is required")
    private String orderId;

    @NotNull(message = "Target ID is required")
    private String targetId;

    @NotNull(message = "Target type is required")
    private Rating.RatingTargetType targetType;

    @NotNull(message = "Score is required")
    @Min(value = 1, message = "Score must be at least 1")
    @Max(value = 5, message = "Score must be at most 5")
    private Integer score;

    private String reviewText;

    private List<Rating.FeedbackCategory> feedbackCategories;
}
