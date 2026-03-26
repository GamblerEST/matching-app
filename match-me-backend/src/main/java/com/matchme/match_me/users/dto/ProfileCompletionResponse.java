package com.matchme.match_me.users.dto;

public record ProfileCompletionResponse(
    boolean isComplete,
    String message,
    MissingFields missingFields
) {
    public record MissingFields(
        boolean profile,
        boolean bio,
        boolean location
    ) {}
}
