package com.logistics.rating.service;

import com.logistics.platform.event.dto.RatingSubmittedEvent;
import com.logistics.rating.dto.FeedbackSummaryResponse;
import com.logistics.rating.dto.SubmitRatingRequest;
import com.logistics.rating.event.RatingEventProducer;
import com.logistics.rating.model.Rating;
import com.logistics.rating.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingService {
    private final RatingRepository ratingRepository;
    private final ModerationService moderationService;
    private final RatingEventProducer eventProducer;

    /**
     * Submit a new rating with auto-moderation
     */
    @Transactional
    public Rating submitRating(SubmitRatingRequest request) {
        log.info("Submitting rating for target: {} by user: {}", request.getTargetId(), request.getOrderId());

        String currentUserId = "CUSTOMER"; // Fallback
        try {
            if (org.springframework.security.core.context.SecurityContextHolder.getContext()
                    .getAuthentication() != null) {
                currentUserId = org.springframework.security.core.context.SecurityContextHolder.getContext()
                        .getAuthentication().getName();
            }
        } catch (Exception e) {
            log.warn("Could not retrieve current user from security context", e);
        }

        Rating rating = Rating.builder()
                .orderId(request.getOrderId())
                .userId(currentUserId)
                .targetId(request.getTargetId())
                .targetType(request.getTargetType())
                .score(request.getScore())
                .reviewText(request.getReviewText())
                .feedbackCategories(request.getFeedbackCategories())
                .build();

        Rating savedRating = ratingRepository.save(rating);

        // Auto-moderate the rating
        Rating moderatedRating = moderationService.autoModerate(savedRating);

        // Publish event
        RatingSubmittedEvent event = RatingSubmittedEvent.create(
                request.getOrderId(),
                currentUserId,
                request.getTargetId(),
                request.getTargetType().name(),
                request.getScore(),
                request.getReviewText());
        eventProducer.publishRatingSubmitted(event);

        return moderatedRating;
    }

    @Transactional
    public Rating createRating(Rating rating) {
        log.info("Creating rating for target: {} by user: {}", rating.getTargetId(), rating.getUserId());
        return ratingRepository.save(rating);
    }

    public List<Rating> getRatingsForTarget(String targetId, Rating.RatingTargetType targetType) {
        return ratingRepository.findByTargetIdAndTargetType(targetId, targetType);
    }

    /**
     * Get only approved ratings for a target
     */
    public List<Rating> getApprovedRatings(String targetId, Rating.RatingTargetType targetType) {
        List<Rating.ModerationStatus> approvedStatuses = Arrays.asList(
                Rating.ModerationStatus.APPROVED,
                Rating.ModerationStatus.AUTO_APPROVED);
        return ratingRepository.findByTargetIdAndTargetTypeAndModerationStatusIn(
                targetId, targetType, approvedStatuses);
    }

    public Double getAverageRating(String targetId, Rating.RatingTargetType targetType) {
        List<Rating> ratings = getApprovedRatings(targetId, targetType);
        if (ratings.isEmpty()) {
            return 0.0;
        }
        return ratings.stream()
                .mapToInt(Rating::getScore)
                .average()
                .orElse(0.0);
    }

    /**
     * Get feedback category summary for a target
     */
    public FeedbackSummaryResponse getFeedbackCategorySummary(String targetId, Rating.RatingTargetType targetType) {
        List<Rating> approvedRatings = getApprovedRatings(targetId, targetType);

        Map<Rating.FeedbackCategory, Long> categoryCounts = approvedRatings.stream()
                .flatMap(r -> r.getFeedbackCategories().stream())
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

        return FeedbackSummaryResponse.builder()
                .categoryCounts(categoryCounts)
                .averageRating(getAverageRating(targetId, targetType))
                .totalRatings((long) ratingRepository.findByTargetIdAndTargetType(targetId, targetType).size())
                .approvedRatings((long) approvedRatings.size())
                .build();
    }
}
