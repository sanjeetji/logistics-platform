package com.logistics.sla.repository;

import com.logistics.sla.model.SLABreachPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SLABreachPredictionRepository extends JpaRepository<SLABreachPrediction, Long> {

    Optional<SLABreachPrediction> findBySlaInstanceId(String slaInstanceId);

    List<SLABreachPrediction> findByStatus(SLABreachPrediction.PredictionStatus status);

    List<SLABreachPrediction> findByRiskLevel(SLABreachPrediction.RiskLevel riskLevel);

    List<SLABreachPrediction> findByEntityIdAndStatus(String entityId, SLABreachPrediction.PredictionStatus status);
}
