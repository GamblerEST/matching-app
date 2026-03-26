package com.matchme.match_me.location.dto;

public record LocationResponse(
        Long id,
        Long userId,
        Double latitude,
        Double longitude,
        Double maxRadiusKm,
        String city
) {}
