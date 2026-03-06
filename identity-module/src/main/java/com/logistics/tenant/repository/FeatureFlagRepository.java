package com.logistics.tenant.repository;

import com.logistics.tenant.model.FeatureFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeatureFlagRepository extends JpaRepository<FeatureFlag, Long> {

    Optional<FeatureFlag> findByFeatureKey(String featureKey);

    List<FeatureFlag> findAllByOrderByCategoryAscFeatureNameAsc();

    List<FeatureFlag> findByFeatureKeyIn(List<String> featureKeys);

    List<FeatureFlag> findByCategoryOrderByFeatureNameAsc(String category);

    boolean existsByFeatureKey(String featureKey);
}
