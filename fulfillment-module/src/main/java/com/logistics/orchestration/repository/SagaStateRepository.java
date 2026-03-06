package com.logistics.orchestration.repository;

import com.logistics.orchestration.model.SagaState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SagaStateRepository extends JpaRepository<SagaState, String> {
}
