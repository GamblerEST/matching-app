package com.matchme.match_me.users.dto;

public record CreateUserRequest(
        String email,
        String password
) {}