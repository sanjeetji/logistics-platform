package com.logistics.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Defines a time range for analytics queries
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeRange {
    /**
     * Start time (inclusive)
     */
    private LocalDateTime startTime;

    /**
     * End time (inclusive)
     */
    private LocalDateTime endTime;

    /**
     * Validate time range
     */
    public boolean isValid() {
        return startTime != null && endTime != null && startTime.isBefore(endTime);
    }
}
