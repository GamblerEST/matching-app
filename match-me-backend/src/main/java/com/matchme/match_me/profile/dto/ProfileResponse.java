package com.matchme.match_me.profile.dto;

public record ProfileResponse(
        Long id,
        Long userId,
        String displayName,
        String aboutMe,
        String avatarUrl
) {}