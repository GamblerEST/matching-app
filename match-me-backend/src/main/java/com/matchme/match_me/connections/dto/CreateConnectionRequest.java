package com.matchme.match_me.connections.dto;

public record CreateConnectionRequest(
        Long requesterId,
        Long targetId
) {}