package com.matchme.match_me.recommendations;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "dismissed_recommendations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "dismissed_user_id"})
})
public class DismissedRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long dismissedUserId;
    private LocalDateTime dismissedAt;

    @PrePersist
    void onCreate() {
        dismissedAt = LocalDateTime.now();
    }

    // ===== Getters & Setters =====
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDismissedUserId() {
        return dismissedUserId;
    }

    public void setDismissedUserId(Long dismissedUserId) {
        this.dismissedUserId = dismissedUserId;
    }

    public LocalDateTime getDismissedAt() {
        return dismissedAt;
    }
}
