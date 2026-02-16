package com.logistics.rating.repository;

import com.logistics.rating.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByTargetId(String targetId);

    List<Rating> findByTargetIdAndTargetType(String targetId, Rating.RatingTargetType targetType);

    List<Rating> findByModerationStatus(Rating.ModerationStatus status);

    List<Rating> findByTargetIdAndTargetTypeAndModerationStatusIn(
            String targetId,
            Rating.RatingTargetType targetType,
            List<Rating.ModerationStatus> statuses);
}
