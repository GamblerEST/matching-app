package com.matchme.match_me.recommendations.dto;

public record DismissRecommendationResponse(
    String message,
    Long dismissedUserId
) {}
