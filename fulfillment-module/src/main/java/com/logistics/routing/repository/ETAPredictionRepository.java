package com.logistics.routing.repository;

import com.logistics.routing.model.ETAPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ETAPredictionRepository extends JpaRepository<ETAPrediction, String> {
}
