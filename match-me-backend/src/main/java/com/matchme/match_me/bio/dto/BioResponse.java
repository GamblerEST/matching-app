package com.matchme.match_me.bio.dto;

public record BioResponse(
        Long id,
        Long userId,
        String hobbies,
        String interests,
        String foodPreferences,
        String musicTaste,
        String personalityType,
        String lookingFor
) {}
