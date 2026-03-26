package com.matchme.match_me.chat.dto;

public record SendMessageRequest(
    Long chatRoomId,
    String message
) {}
