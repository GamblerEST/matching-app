package com.matchme.match_me.chat.dto;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long id,
        Long chatRoomId,
        Long senderId,
        Long receiverId,
        String message,
        boolean seen,
        LocalDateTime timestamp
) {}
