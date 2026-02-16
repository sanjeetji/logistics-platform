package com.logistics.rating.controller;

import com.logistics.platform.common.dto.response.ApiResponse;
import com.logistics.rating.model.Rating;
import com.logistics.rating.service.ModerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ratings/moderation")
@RequiredArgsConstructor
public class ModerationController {

    private final ModerationService moderationService;

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<Rating>>> getPendingReviews() {
        return ResponseEntity.ok(ApiResponse.success(
                moderationService.getPendingReviews(),
                "Pending reviews retrieved successfully"));
    }

    @PostMapping("/{ratingId}/approve")
    public ResponseEntity<ApiResponse<Rating>> approveReview(
            @PathVariable Long ratingId,
            @RequestParam(defaultValue = "ADMIN") String moderatorId) {
        return ResponseEntity.ok(ApiResponse.success(
                moderationService.approveReview(ratingId, moderatorId),
                "Review approved successfully"));
    }

    @PostMapping("/{ratingId}/reject")
    public ResponseEntity<ApiResponse<Rating>> rejectReview(
            @PathVariable Long ratingId,
            @RequestParam(defaultValue = "ADMIN") String moderatorId,
            @RequestBody String reason) {
        return ResponseEntity.ok(ApiResponse.success(
                moderationService.rejectReview(ratingId, moderatorId, reason),
                "Review rejected successfully"));
    }
}
