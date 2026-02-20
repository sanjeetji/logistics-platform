package com.logistics.dispatch.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.dispatch.dto.DispatchRequest;
import com.logistics.dispatch.service.DispatchService;
import com.logistics.dispatch.event.DispatchEventProducer;
import com.logistics.platform.event.dto.OrchestrationCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DispatchCommandListener {

    private final DispatchService dispatchService;
    private final DispatchEventProducer eventProducer;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "orchestration.command.dispatch", groupId = "dispatch-service-group")
    public void handleDispatchCommand(OrchestrationCommand command) {
        log.info("Received Dispatch Command: {}", command.getCommandId());

        if (command.getType() != OrchestrationCommand.CommandType.DISPATCH_ORDER) {
            log.warn("Ignoring unsupported command type: {}", command.getType());
            return;
        }

        try {
            Map<String, Object> payload = command.getPayload();
            // Assuming payload contains necessary fields or a full DispatchRequest object
            // For now, we manually map specific fields expected in DispatchRequest

            DispatchRequest request = DispatchRequest.builder()
                    .orderId((String) payload.get("orderId"))
                    .pickupLatitude(Double.valueOf(payload.getOrDefault("pickupLat", "0.0").toString()))
                    .pickupLongitude(Double.valueOf(payload.getOrDefault("pickupLng", "0.0").toString()))
                    .dropLatitude(Double.valueOf(payload.getOrDefault("dropLat", "0.0").toString()))
                    .dropLongitude(Double.valueOf(payload.getOrDefault("dropLng", "0.0").toString()))
                    .weightKg(Double.valueOf(payload.getOrDefault("weightKg", "0.0").toString()))
                    .vehicleTypePreference((String) payload.get("vehicleType"))
                    .build();

            log.info("Executing Auto-Dispatch for Order: {}", request.getOrderId());
            dispatchService.autoDispatch(request);

        } catch (Exception e) {
            log.error("Failed to process dispatch command for ID: {}", command.getCommandId(), e);
            String orderId = command.getPayload() != null ? (String) command.getPayload().get("orderId") : "UNKNOWN";
            eventProducer.publishAssignmentFailure(orderId, e.getMessage());
        }
    }
}
