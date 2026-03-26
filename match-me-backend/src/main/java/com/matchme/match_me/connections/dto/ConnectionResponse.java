package com.matchme.match_me.connections.dto;

import com.matchme.match_me.connections.ConnectionStatus;

public record ConnectionResponse(
        Long id,
        Long requesterId,
        Long targetId,
        ConnectionStatus status
) {}