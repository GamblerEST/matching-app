package com.matchme.match_me.location.dto;

public record UpdateLocationRequest(
        Double latitude,
        Double longitude,
        Double maxRadiusKm,
        String city
) {}
