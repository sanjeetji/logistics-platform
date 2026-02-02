package com.logistics.pricing.repository;

import com.logistics.pricing.model.RateCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RateCardRepository extends JpaRepository<RateCard, UUID> {
    
    Optional<RateCard> findByTenantIdAndVehicleTypeAndType(String tenantId, String vehicleType, RateCard.PricingType type);

    Optional<RateCard> findByTypeAndVehicleType(RateCard.PricingType type, String vehicleType);
}
