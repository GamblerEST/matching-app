package com.matchme.match_me.chat.dto;

import java.time.LocalDateTime;

public record ChatSummaryResponse(
    Long chatRoomId,
    Long otherUserId,
    String lastMessage,
    LocalDateTime lastMessageTime,
    long unreadCount
) {}
