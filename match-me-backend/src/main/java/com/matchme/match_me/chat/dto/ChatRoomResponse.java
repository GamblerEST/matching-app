package com.matchme.match_me.chat.dto;

public record ChatRoomResponse(
        Long id,
        Long userA,
        Long userB
) {}