package com.matchme.match_me.connections.dto;

import com.matchme.match_me.connections.ConnectionStatus;

public record UpdateConnectionRequest(
        ConnectionStatus status
) {}