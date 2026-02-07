package com.logistics.pricing.service;

import com.logistics.pricing.model.PricingRule;
import com.logistics.pricing.model.SurgeZone;
import com.logistics.pricing.repository.PricingRuleRepository;
import com.logistics.pricing.repository.SurgeZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Admin service for managing pricing rules and surge zones
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PricingAdminService {

    private final PricingRuleRepository pricingRuleRepository;
    private final SurgeZoneRepository surgeZoneRepository;

    // ===== Pricing Rules =====

    @Transactional
    public PricingRule createPricingRule(PricingRule rule) {
        log.info("Creating pricing rule for vehicle type: {}", rule.getVehicleType());
        return pricingRuleRepository.save(rule);
    }

    @Transactional
    public PricingRule updatePricingRule(Long id, PricingRule updatedRule) {
        PricingRule existing = pricingRuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pricing rule not found: " + id));

        existing.setBaseFare(updatedRule.getBaseFare());
        existing.setPerKmRate(updatedRule.getPerKmRate());
        existing.setPerMinuteRate(updatedRule.getPerMinuteRate());
        existing.setMinimumFare(updatedRule.getMinimumFare());
        existing.setMaximumFare(updatedRule.getMaximumFare());
        existing.setActive(updatedRule.getActive());
        existing.setEffectiveFrom(updatedRule.getEffectiveFrom());
        existing.setEffectiveTo(updatedRule.getEffectiveTo());
        existing.setDescription(updatedRule.getDescription());

        return pricingRuleRepository.save(existing);
    }

    public List<PricingRule> getAllPricingRules() {
        return pricingRuleRepository.findAll();
    }

    public List<PricingRule> getActivePricingRules() {
        return pricingRuleRepository.findByActive(true);
    }

    public PricingRule getPricingRuleById(Long id) {
        return pricingRuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pricing rule not found: " + id));
    }

    @Transactional
    public void deletePricingRule(Long id) {
        pricingRuleRepository.deleteById(id);
        log.info("Deleted pricing rule: {}", id);
    }

    // ===== Surge Zones =====

    @Transactional
    public SurgeZone createSurgeZone(SurgeZone zone) {
        log.info("Creating surge zone: {}", zone.getZoneName());
        return surgeZoneRepository.save(zone);
    }

    @Transactional
    public SurgeZone updateSurgeZone(Long id, SurgeZone updatedZone) {
        SurgeZone existing = surgeZoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Surge zone not found: " + id));

        existing.setZoneName(updatedZone.getZoneName());
        existing.setLatitude(updatedZone.getLatitude());
        existing.setLongitude(updatedZone.getLongitude());
        existing.setRadiusKm(updatedZone.getRadiusKm());
        existing.setSurgeMultiplier(updatedZone.getSurgeMultiplier());
        existing.setActiveFrom(updatedZone.getActiveFrom());
        existing.setActiveTo(updatedZone.getActiveTo());
        existing.setReason(updatedZone.getReason());
        existing.setActive(updatedZone.getActive());

        return surgeZoneRepository.save(existing);
    }

    public List<SurgeZone> getAllSurgeZones() {
        return surgeZoneRepository.findAll();
    }

    public List<SurgeZone> getActiveSurgeZones() {
        return surgeZoneRepository.findByActive(true);
    }

    public SurgeZone getSurgeZoneById(Long id) {
        return surgeZoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Surge zone not found: " + id));
    }

    @Transactional
    public void deleteSurgeZone(Long id) {
        surgeZoneRepository.deleteById(id);
        log.info("Deleted surge zone: {}", id);
    }
}
