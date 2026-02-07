package com.logistics.dispatch.model;

public enum AssignmentStatus {
    PENDING, // Assignment created, waiting for driver response
    ACCEPTED, // Driver accepted the assignment
    REJECTED, // Driver rejected the assignment
    AUTO_ASSIGNED, // Automatically assigned (no driver action required)
    COMPLETED, // Assignment completed
    CANCELLED // Assignment cancelled
}
