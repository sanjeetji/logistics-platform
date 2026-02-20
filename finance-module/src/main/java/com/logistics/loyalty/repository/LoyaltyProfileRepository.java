package com.logistics.loyalty.repository;

import com.logistics.loyalty.model.LoyaltyProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoyaltyProfileRepository extends JpaRepository<LoyaltyProfile, Long> {
    Optional<LoyaltyProfile> findByUserId(String userId);
}
