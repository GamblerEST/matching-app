package com.matchme.match_me.chat.dto;

public record CreateChatMessageRequest(
        Long chatRoomId,
        Long senderId,
        Long receiverId,
        String message
) {}