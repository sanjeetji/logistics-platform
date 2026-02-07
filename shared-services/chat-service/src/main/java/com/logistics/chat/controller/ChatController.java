package com.logistics.chat.controller;

import com.logistics.chat.model.ChatMessage;
import com.logistics.chat.model.ChatRoom;
import com.logistics.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * WebSocket endpoint: Clients send messages to /app/chat/{roomId}
     * Server broadcasts to /topic/room/{roomId}
     */
    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public ChatMessage sendMessage(@DestinationVariable String roomId, ChatMessage message) {
        message.setRoomId(roomId);
        return chatService.saveMessage(message);
    }

    /**
     * REST endpoint: Get message history for a room
     */
    @GetMapping("/api/v1/chat/history/{roomId}")
    @ResponseBody
    public ResponseEntity<List<ChatMessage>> getHistory(@PathVariable String roomId) {
        return ResponseEntity.ok(chatService.getMessageHistory(roomId));
    }

    /**
     * REST endpoint: Create or get a chat room
     */
    @PostMapping("/api/v1/chat/room")
    @ResponseBody
    public ResponseEntity<ChatRoom> createRoom(@RequestBody ChatRoomRequest request) {
        ChatRoom room = chatService.createOrGetRoom(
                request.getOrderId(),
                request.getDriverId(),
                request.getCustomerId()
        );
        return ResponseEntity.ok(room);
    }

    // Simple DTO for room creation
    public static class ChatRoomRequest {
        private String orderId;
        private String driverId;
        private String customerId;

        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        public String getDriverId() { return driverId; }
        public void setDriverId(String driverId) { this.driverId = driverId; }
        public String getCustomerId() { return customerId; }
        public void setCustomerId(String customerId) { this.customerId = customerId; }
    }
}
