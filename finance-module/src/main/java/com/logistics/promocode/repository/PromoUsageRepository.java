package com.logistics.promocode.repository;

import com.logistics.promocode.model.PromoUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromoUsageRepository extends JpaRepository<PromoUsage, Long> {
    List<PromoUsage> findByUserId(String userId);
    List<PromoUsage> findByPromoCodeId(Long promoCodeId);
    boolean existsByPromoCodeIdAndUserId(Long promoCodeId, String userId);
}
