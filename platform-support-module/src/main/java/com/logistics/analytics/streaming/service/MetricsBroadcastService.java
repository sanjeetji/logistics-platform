package com.logistics.analytics.streaming.service;

import com.logistics.analytics.streaming.model.DashboardData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsBroadcastService {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final MetricsStorageService metricsStorageService;
    
    /**
     * Broadcast metrics to all connected WebSocket clients every 10 seconds
     */
    @Scheduled(fixedRate = 10000) // 10 seconds
    public void broadcastMetrics() {
        try {
            DashboardData dashboard = DashboardData.builder()
                    .orderMetrics(metricsStorageService.getOrderMetrics())
                    .driverMetrics(metricsStorageService.getDriverMetrics())
                    .revenueMetrics(metricsStorageService.getRevenueMetrics())
                    .slaMetrics(metricsStorageService.getSLAMetrics())
                    .build();
            
            messagingTemplate.convertAndSend("/topic/metrics", dashboard);
            log.debug("Broadcasted metrics to WebSocket clients");
            
        } catch (Exception e) {
            log.error("Error broadcasting metrics", e);
        }
    }
}
