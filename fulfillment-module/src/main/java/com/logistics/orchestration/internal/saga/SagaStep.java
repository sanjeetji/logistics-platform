package com.logistics.orchestration.internal.saga;

import com.logistics.orchestration.internal.domain.SagaInstance;

public interface SagaStep {
    String getStepName();

    void execute(SagaInstance saga);

    void compensate(SagaInstance saga);
}
