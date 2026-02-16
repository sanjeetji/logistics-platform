package com.logistics.rating.service;

import com.logistics.platform.event.dto.ReviewModeratedEvent;
import com.logistics.rating.event.RatingEventProducer;
import com.logistics.rating.model.Rating;
import com.logistics.rating.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModerationService {

    private final RatingRepository ratingRepository;
    private final RatingEventProducer eventProducer;

    // Simple profanity filter - in production, use a more sophisticated library
    private static final Set<String> PROFANITY_KEYWORDS = new HashSet<>(Arrays.asList(
            "badword1", "badword2", "offensive", "inappropriate"
    // Add more keywords as needed
    ));

    /**
     * Check if text contains profanity
     */
    public boolean containsProfanity(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }

        String lowerText = text.toLowerCase();
        return PROFANITY_KEYWORDS.stream()
                .anyMatch(lowerText::contains);
    }

    /**
     * Auto-moderate a rating based on content
     */
    @Transactional
    public Rating autoModerate(Rating rating) {
        if (rating.getReviewText() == null || rating.getReviewText().isBlank()) {
            // No review text, auto-approve
            rating.setModerationStatus(Rating.ModerationStatus.AUTO_APPROVED);
            rating.setModeratedAt(LocalDateTime.now());
            rating.setModeratedBy("SYSTEM");
            log.info("Auto-approved rating {} (no review text)", rating.getId());
        } else if (containsProfanity(rating.getReviewText())) {
            // Contains profanity, mark as pending
            rating.setModerationStatus(Rating.ModerationStatus.PENDING);
            log.warn("Rating {} flagged for moderation (profanity detected)", rating.getId());
        } else {
            // Clean review, auto-approve
            rating.setModerationStatus(Rating.ModerationStatus.AUTO_APPROVED);
            rating.setModeratedAt(LocalDateTime.now());
            rating.setModeratedBy("SYSTEM");
            log.info("Auto-approved rating {} (clean review)", rating.getId());
        }

        return ratingRepository.save(rating);
    }

    /**
     * Manually approve a review
     */
    @Transactional
    public Rating approveReview(Long ratingId, String moderatorId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new RuntimeException("Rating not found: " + ratingId));

        rating.setModerationStatus(Rating.ModerationStatus.APPROVED);
        rating.setModeratedAt(LocalDateTime.now());
        rating.setModeratedBy(moderatorId);

        Rating savedRating = ratingRepository.save(rating);

        // Publish event
        ReviewModeratedEvent event = ReviewModeratedEvent.create(
                ratingId,
                Rating.ModerationStatus.APPROVED.name(),
                moderatorId,
                LocalDateTime.now(),
                null);
        eventProducer.publishReviewModerated(event);

        log.info("Rating {} approved by {}", ratingId, moderatorId);
        return savedRating;
    }

    /**
     * Manually reject a review
     */
    @Transactional
    public Rating rejectReview(Long ratingId, String moderatorId, String reason) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new RuntimeException("Rating not found: " + ratingId));

        rating.setModerationStatus(Rating.ModerationStatus.REJECTED);
        rating.setModerationReason(reason);
        rating.setModeratedAt(LocalDateTime.now());
        rating.setModeratedBy(moderatorId);

        Rating savedRating = ratingRepository.save(rating);

        // Publish event
        ReviewModeratedEvent event = ReviewModeratedEvent.create(
                ratingId,
                Rating.ModerationStatus.REJECTED.name(),
                moderatorId,
                LocalDateTime.now(),
                reason);
        eventProducer.publishReviewModerated(event);

        log.info("Rating {} rejected by {} - Reason: {}", ratingId, moderatorId, reason);
        return savedRating;
    }

    /**
     * Get all pending reviews for moderation
     */
    public java.util.List<Rating> getPendingReviews() {
        return ratingRepository.findByModerationStatus(Rating.ModerationStatus.PENDING);
    }
}
