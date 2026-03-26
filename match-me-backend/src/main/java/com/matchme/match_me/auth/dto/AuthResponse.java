package com.matchme.match_me.auth.dto;

public record AuthResponse(
        Long userId,
        String token
) {}