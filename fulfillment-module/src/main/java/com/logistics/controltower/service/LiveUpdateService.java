package com.logistics.controltower.service;

import com.logistics.controltower.model.DashboardMetric;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LiveUpdateService {

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastMetric(DashboardMetric metric) {
        log.debug("Broadcasting metric: {} = {}", metric.getMetricName(), metric.getValue());
        messagingTemplate.convertAndSend("/topic/dashboard/metrics", metric);
    }

    public void broadcastAlert(Object alertPayload) {
        log.info("Broadcasting alert: {}", alertPayload);
        messagingTemplate.convertAndSend("/topic/alerts", alertPayload);
    }
}
