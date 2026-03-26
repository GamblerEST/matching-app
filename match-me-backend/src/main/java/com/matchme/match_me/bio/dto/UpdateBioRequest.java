package com.matchme.match_me.bio.dto;

public record UpdateBioRequest(
        String hobbies,
        String interests,
        String foodPreferences,
        String musicTaste,
        String personalityType,
        String lookingFor
) {}