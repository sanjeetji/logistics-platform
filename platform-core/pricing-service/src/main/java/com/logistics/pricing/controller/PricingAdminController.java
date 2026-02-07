package com.logistics.pricing.controller;

import com.logistics.platform.common.dto.response.ApiResponse;
import com.logistics.pricing.model.PricingRule;
import com.logistics.pricing.model.SurgeZone;
import com.logistics.pricing.service.PricingAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pricing/admin")
@RequiredArgsConstructor
public class PricingAdminController {

    private final PricingAdminService adminService;

    // ===== Pricing Rules =====

    @PostMapping("/rules")
    public ResponseEntity<ApiResponse<PricingRule>> createPricingRule(@RequestBody PricingRule rule) {
        PricingRule created = adminService.createPricingRule(rule);
        return ResponseEntity.ok(ApiResponse.success(created, "Pricing rule created successfully"));
    }

    @PutMapping("/rules/{id}")
    public ResponseEntity<ApiResponse<PricingRule>> updatePricingRule(
            @PathVariable Long id,
            @RequestBody PricingRule rule) {
        PricingRule updated = adminService.updatePricingRule(id, rule);
        return ResponseEntity.ok(ApiResponse.success(updated, "Pricing rule updated successfully"));
    }

    @GetMapping("/rules")
    public ResponseEntity<ApiResponse<List<PricingRule>>> getAllPricingRules() {
        List<PricingRule> rules = adminService.getAllPricingRules();
        return ResponseEntity.ok(ApiResponse.success(rules));
    }

    @GetMapping("/rules/active")
    public ResponseEntity<ApiResponse<List<PricingRule>>> getActivePricingRules() {
        List<PricingRule> rules = adminService.getActivePricingRules();
        return ResponseEntity.ok(ApiResponse.success(rules));
    }

    @GetMapping("/rules/{id}")
    public ResponseEntity<ApiResponse<PricingRule>> getPricingRule(@PathVariable Long id) {
        PricingRule rule = adminService.getPricingRuleById(id);
        return ResponseEntity.ok(ApiResponse.success(rule));
    }

    @DeleteMapping("/rules/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePricingRule(@PathVariable Long id) {
        adminService.deletePricingRule(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Pricing rule deleted successfully"));
    }

    // ===== Surge Zones =====

    @PostMapping("/surge-zones")
    public ResponseEntity<ApiResponse<SurgeZone>> createSurgeZone(@RequestBody SurgeZone zone) {
        SurgeZone created = adminService.createSurgeZone(zone);
        return ResponseEntity.ok(ApiResponse.success(created, "Surge zone created successfully"));
    }

    @PutMapping("/surge-zones/{id}")
    public ResponseEntity<ApiResponse<SurgeZone>> updateSurgeZone(
            @PathVariable Long id,
            @RequestBody SurgeZone zone) {
        SurgeZone updated = adminService.updateSurgeZone(id, zone);
        return ResponseEntity.ok(ApiResponse.success(updated, "Surge zone updated successfully"));
    }

    @GetMapping("/surge-zones")
    public ResponseEntity<ApiResponse<List<SurgeZone>>> getAllSurgeZones() {
        List<SurgeZone> zones = adminService.getAllSurgeZones();
        return ResponseEntity.ok(ApiResponse.success(zones));
    }

    @GetMapping("/surge-zones/active")
    public ResponseEntity<ApiResponse<List<SurgeZone>>> getActiveSurgeZones() {
        List<SurgeZone> zones = adminService.getActiveSurgeZones();
        return ResponseEntity.ok(ApiResponse.success(zones));
    }

    @GetMapping("/surge-zones/{id}")
    public ResponseEntity<ApiResponse<SurgeZone>> getSurgeZone(@PathVariable Long id) {
        SurgeZone zone = adminService.getSurgeZoneById(id);
        return ResponseEntity.ok(ApiResponse.success(zone));
    }

    @DeleteMapping("/surge-zones/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSurgeZone(@PathVariable Long id) {
        adminService.deleteSurgeZone(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Surge zone deleted successfully"));
    }
}
