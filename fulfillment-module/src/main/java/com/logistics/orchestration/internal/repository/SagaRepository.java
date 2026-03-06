package com.logistics.orchestration.internal.repository;

import com.logistics.orchestration.internal.domain.SagaInstance;
import com.logistics.orchestration.internal.domain.SagaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SagaRepository extends JpaRepository<SagaInstance, UUID> {
    Optional<SagaInstance> findByCorrelationId(String correlationId);

    List<SagaInstance> findByStatus(SagaStatus status);
}
