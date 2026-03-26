package com.matchme.match_me.chat;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chatRoomId;
    private Long senderId;

    @Column(nullable = false, length = 2000)
    private String message;

    private boolean seen;

    private LocalDateTime timestamp;

    @PrePersist
    void onCreate() {
        timestamp = LocalDateTime.now();
    }

    // Getters / setters
    public Long getId() {
        return id;
    }

    public Long getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(Long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // optional helper to mark as seen
    public void markSeen() {
        this.seen = true;
    }
}