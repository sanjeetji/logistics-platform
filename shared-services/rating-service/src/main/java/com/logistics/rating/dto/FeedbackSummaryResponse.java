package com.logistics.rating.dto;

import com.logistics.rating.model.Rating;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackSummaryResponse {

    private Map<Rating.FeedbackCategory, Long> categoryCounts;
    private Double averageRating;
    private Long totalRatings;
    private Long approvedRatings;
}
