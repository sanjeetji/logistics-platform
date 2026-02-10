package com.logistics.analytics.repository;

import com.logistics.analytics.model.AnalyticsEvent;
import com.logistics.analytics.model.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnalyticsEventRepository extends JpaRepository<AnalyticsEvent, Long> {
    
    List<AnalyticsEvent> findByEventType(EventType eventType);
    
    List<AnalyticsEvent> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(e) FROM AnalyticsEvent e WHERE e.eventType = :eventType AND e.timestamp BETWEEN :start AND :end")
    Long countByEventTypeAndTimestampBetween(@Param("eventType") EventType eventType, 
                                             @Param("start") LocalDateTime start, 
                                             @Param("end") LocalDateTime end);
    
    @Query("SELECT e.aggregationKey, COUNT(e) FROM AnalyticsEvent e WHERE e.eventType = :eventType AND e.timestamp BETWEEN :start AND :end GROUP BY e.aggregationKey")
    List<Object[]> countByAggregationKey(@Param("eventType") EventType eventType,
                                        @Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);
}
