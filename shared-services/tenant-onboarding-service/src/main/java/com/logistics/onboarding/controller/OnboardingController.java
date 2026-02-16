package com.logistics.onboarding.controller;

import com.logistics.onboarding.dto.*;
import com.logistics.onboarding.service.OnboardingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
@Tag(name = "Tenant Onboarding", description = "Self-service tenant onboarding and subscription management")
public class OnboardingController {
    
    private final OnboardingService onboardingService;
    
    @PostMapping("/start")
    @Operation(summary = "Start onboarding process")
    public ResponseEntity<OnboardingResponse> startOnboarding(@Valid @RequestBody StartOnboardingRequest request) {
        OnboardingResponse response = onboardingService.startOnboarding(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{onboardingId}/subscription")
    @Operation(summary = "Setup subscription and payment")
    public ResponseEntity<OnboardingResponse> setupSubscription(
            @PathVariable Long onboardingId,
            @Valid @RequestBody SubscriptionRequest request) {
        OnboardingResponse response = onboardingService.setupSubscription(onboardingId, request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{onboardingId}/complete")
    @Operation(summary = "Complete onboarding")
    public ResponseEntity<OnboardingResponse> completeOnboarding(@PathVariable Long onboardingId) {
        OnboardingResponse response = onboardingService.completeOnboarding(onboardingId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{onboardingId}")
    @Operation(summary = "Get onboarding status")
    public ResponseEntity<OnboardingResponse> getOnboarding(@PathVariable Long onboardingId) {
        OnboardingResponse response = onboardingService.getOnboarding(onboardingId);
        return ResponseEntity.ok(response);
    }
}
