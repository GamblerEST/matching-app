package com.matchme.match_me.profile.dto;

public record UpdateProfileRequest(
        String displayName,
        String aboutMe,
        String avatarUrl,
        boolean complete
) {}