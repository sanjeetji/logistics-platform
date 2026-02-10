package com.logistics.chat.controller;

import com.logistics.chat.model.ChatMessage;
import com.logistics.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Handle incoming chat messages via WebSocket
     * Clients send to: /app/chat/send
     */
    @MessageMapping("/chat/send")
    public void handleChatMessage(@Payload ChatMessage message) {
        log.debug("Received chat message from {} in room {}", message.getSenderId(), message.getRoomId());

        message.setTimestamp(LocalDateTime.now());

        // Save message to database
        ChatMessage savedMessage = chatService.saveMessage(message);

        // Broadcast to room subscribers
        messagingTemplate.convertAndSend(
                "/topic/chat/" + message.getRoomId(),
                savedMessage);

        // Send to specific recipient if needed
        if (message.getRecipientId() != null) {
            messagingTemplate.convertAndSendToUser(
                    message.getRecipientId(),
                    "/queue/messages",
                    savedMessage);
        }
    }
}
