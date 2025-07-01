package com.menejementpj.model;

import java.time.LocalDateTime;

public class ChatMessage {
    private final int senderId;
    private final String senderName;
    private final String message;
    private final LocalDateTime timestamp;

    public ChatMessage(int senderId, String senderName, String message, LocalDateTime timestamp) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}