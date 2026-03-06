package com.logistics.rating.controller;

import com.logistics.platform.common.dto.response.ApiResponse;
import com.logistics.rating.dto.FeedbackSummaryResponse;
import com.logistics.rating.dto.SubmitRatingRequest;
import com.logistics.rating.model.Rating;
import com.logistics.rating.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ratings")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<Rating>> submitRating(@Valid @RequestBody SubmitRatingRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                ratingService.submitRating(request),
                "Rating submitted successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Rating>> createRating(@RequestBody Rating rating) {
        return ResponseEntity
                .ok(ApiResponse.success(ratingService.createRating(rating), "Rating submitted successfully"));
    }

    @GetMapping("/target/{targetId}")
    public ResponseEntity<ApiResponse<List<Rating>>> getRatings(
            @PathVariable String targetId,
            @RequestParam Rating.RatingTargetType type) {
        return ResponseEntity.ok(ApiResponse.success(ratingService.getRatingsForTarget(targetId, type)));
    }

    @GetMapping("/target/{targetId}/approved")
    public ResponseEntity<ApiResponse<List<Rating>>> getApprovedRatings(
            @PathVariable String targetId,
            @RequestParam Rating.RatingTargetType type) {
        return ResponseEntity.ok(ApiResponse.success(ratingService.getApprovedRatings(targetId, type)));
    }

    @GetMapping("/target/{targetId}/average")
    public ResponseEntity<ApiResponse<Double>> getAverageRating(
            @PathVariable String targetId,
            @RequestParam Rating.RatingTargetType type) {
        return ResponseEntity.ok(ApiResponse.success(ratingService.getAverageRating(targetId, type)));
    }

    @GetMapping("/target/{targetId}/feedback-summary")
    public ResponseEntity<ApiResponse<FeedbackSummaryResponse>> getFeedbackSummary(
            @PathVariable String targetId,
            @RequestParam Rating.RatingTargetType type) {
        return ResponseEntity.ok(ApiResponse.success(ratingService.getFeedbackCategorySummary(targetId, type)));
    }
}
