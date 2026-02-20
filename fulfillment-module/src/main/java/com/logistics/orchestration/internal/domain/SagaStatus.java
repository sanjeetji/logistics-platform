package com.logistics.orchestration.internal.domain;

public enum SagaStatus {
    STARTED,
    DISPATCH_REQUESTED,
    DISPATCH_ASSIGNED,
    COMPLETED,
    FAILED,
    COMPENSATED
}
