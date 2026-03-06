package com.logistics.chat.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roomId;

    private String senderId;
    private String senderRole; // DRIVER, CUSTOMER, SUPPORT
    private String recipientId; // Optional: for direct messages

    @Column(columnDefinition = "TEXT")
    private String content;

    // File sharing support
    @Enumerated(EnumType.STRING)
    private MessageType messageType = MessageType.TEXT; // TEXT, IMAGE, FILE, DOCUMENT

    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private String mimeType;

    private LocalDateTime timestamp;

    public enum MessageType {
        TEXT, IMAGE, FILE, DOCUMENT
    }

    @Enumerated(EnumType.STRING)
    private MessageStatus status; // SENT, DELIVERED, READ

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
        if (status == null) {
            status = MessageStatus.SENT;
        }
    }
}
