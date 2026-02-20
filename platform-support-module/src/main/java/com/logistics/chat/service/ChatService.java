package com.logistics.chat.service;

import com.logistics.chat.model.ChatMessage;
import com.logistics.chat.model.ChatRoom;
import com.logistics.chat.repository.ChatMessageRepository;
import com.logistics.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRoomRepository roomRepository;
    private final ChatMessageRepository messageRepository;

    @Transactional
    public ChatRoom createOrGetRoom(String orderId, String driverId, String customerId) {
        return roomRepository.findByRoomId(orderId)
                .orElseGet(() -> {
                    ChatRoom room = ChatRoom.builder()
                            .roomId(orderId)
                            .driverId(driverId)
                            .customerId(customerId)
                            .build();
                    return roomRepository.save(java.util.Objects.requireNonNull(room));
                });
    }

    @Transactional
    public ChatMessage saveMessage(ChatMessage message) {
        log.info("Saving message from {} in room {}", message.getSenderId(), message.getRoomId());
        return messageRepository.save(message);
    }

    public List<ChatMessage> getMessageHistory(String roomId) {
        return messageRepository.findByRoomIdOrderByTimestampAsc(roomId);
    }
}
