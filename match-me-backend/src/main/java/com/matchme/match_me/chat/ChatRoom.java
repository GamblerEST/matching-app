package com.matchme.match_me.chat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_rooms")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userA;
    private Long userB;

    // Getters / setters
    public Long getId() {
        return id;
    }

    public Long getUserA() {
        return userA;
    }

    public void setUserA(Long userA) {
        this.userA = userA;
    }

    public Long getUserB() {
        return userB;
    }

    public void setUserB(Long userB) {
        this.userB = userB;
    }
}