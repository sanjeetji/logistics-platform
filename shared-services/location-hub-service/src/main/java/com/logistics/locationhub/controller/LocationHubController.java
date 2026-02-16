package com.logistics.locationhub.controller;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.logistics.locationhub.dto.LocationUpdateDTO;
import com.logistics.locationhub.service.LocationIngestionService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LocationHubController {

    private final SocketIOServer server;
    private final LocationIngestionService ingestionService;

    public LocationHubController(SocketIOServer server, LocationIngestionService ingestionService) {
        this.server = server;
        this.ingestionService = ingestionService;
    }

    @PostConstruct
    private void init() {
        server.addConnectListener(onConnect());
        server.addDisconnectListener(onDisconnect());
        server.addEventListener("location_update", LocationUpdateDTO.class, onLocationUpdate());
        server.addEventListener("join_order", String.class, onJoinOrder());
    }

    private ConnectListener onConnect() {
        return client -> {
            String driverId = client.getHandshakeData().getSingleUrlParam("driverId");
            log.info("Client connected: {} (Driver: {})", client.getSessionId(), driverId);
        };
    }

    private DisconnectListener onDisconnect() {
        return client -> log.info("Client disconnected: {}", client.getSessionId());
    }

    private DataListener<LocationUpdateDTO> onLocationUpdate() {
        return (client, data, ackSender) -> {
            log.debug("Received location update from driver {}: {}, {}",
                    data.getDriverId(), data.getLatitude(), data.getLongitude());

            // Ingest into system
            ingestionService.ingestLocation(data);

            // Broadcast to the order room if present
            if (data.getOrderId() != null && !data.getOrderId().isEmpty()) {
                server.getRoomOperations("order_" + data.getOrderId())
                        .sendEvent("location_broadcast", data);
            }
        };
    }

    private DataListener<String> onJoinOrder() {
        return (client, orderId, ackSender) -> {
            log.info("Client {} joining room for order {}", client.getSessionId(), orderId);
            client.joinRoom("order_" + orderId);
            if (ackSender.isAckRequested()) {
                ackSender.sendAckData("Joined order room: " + orderId);
            }
        };
    }
}
