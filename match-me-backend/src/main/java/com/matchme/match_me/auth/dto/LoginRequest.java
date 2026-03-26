package com.matchme.match_me.auth.dto;

public record LoginRequest(
        String email,
        String password
) {}