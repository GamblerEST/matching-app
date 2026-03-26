package com.matchme.match_me.connections;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.matchme.match_me.chat.ChatMessageRepository;
import com.matchme.match_me.chat.ChatRoom;
import com.matchme.match_me.chat.ChatRoomRepository;
import com.matchme.match_me.common.exception.BadRequestException;
import com.matchme.match_me.common.exception.ConflictException;
import com.matchme.match_me.common.exception.ForbiddenException;
import com.matchme.match_me.common.exception.NotFoundException;
import com.matchme.match_me.connections.dto.ConnectionResponse;
import com.matchme.match_me.connections.dto.CreateConnectionRequest;
import com.matchme.match_me.connections.dto.UpdateConnectionRequest;

@Service
@Transactional
public class ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ConnectionService(ConnectionRepository connectionRepository,
                             ChatRoomRepository chatRoomRepository,
                             ChatMessageRepository chatMessageRepository) {
        this.connectionRepository = connectionRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    public ConnectionResponse createConnection(CreateConnectionRequest request) {
        // Prevent self-connection
        if (request.requesterId().equals(request.targetId())) {
            throw new BadRequestException("Cannot create connection with yourself");
        }

        // Check if connection already exists in either direction
        if (connectionRepository.existsByRequesterIdAndTargetId(request.requesterId(), request.targetId()) ||
            connectionRepository.existsByRequesterIdAndTargetId(request.targetId(), request.requesterId())) {
            throw new ConflictException("Connection already exists");
        }

        Connection connection = new Connection();
        connection.setRequesterId(request.requesterId());
        connection.setTargetId(request.targetId());
        connection.setStatus(ConnectionStatus.REQUESTED);

        Connection saved = connectionRepository.save(connection);
        return mapToResponse(saved);
    }

    public ConnectionResponse updateConnection(Long connectionId, UpdateConnectionRequest request) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new NotFoundException("Connection not found"));

        connection.setStatus(request.status());
        Connection updated = connectionRepository.save(connection);
        return mapToResponse(updated);
    }

    public List<ConnectionResponse> getConnectionsForUser(Long userId) {
        return connectionRepository.findAll().stream()
                .filter(c -> c.getStatus() == ConnectionStatus.ACCEPTED)
                .filter(c -> c.getRequesterId().equals(userId) || c.getTargetId().equals(userId))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ConnectionResponse> getPendingRequestsForUser(Long userId) {
        return connectionRepository.findByTargetIdAndStatus(userId, ConnectionStatus.REQUESTED)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ConnectionResponse acceptConnection(Long connectionId, Long authenticatedUserId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new NotFoundException("Connection not found"));

        // Verify user is the target of the request
        if (!connection.getTargetId().equals(authenticatedUserId)) {
            throw new ForbiddenException("You are not authorized to accept this connection");
        }

        if (connection.getStatus() != ConnectionStatus.REQUESTED) {
            throw new BadRequestException("Connection request is not pending");
        }

        connection.setStatus(ConnectionStatus.ACCEPTED);
        Connection updated = connectionRepository.save(connection);
        return mapToResponse(updated);
    }

    public ConnectionResponse rejectConnection(Long connectionId, Long authenticatedUserId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new NotFoundException("Connection not found"));

        // Verify user is the target of the request
        if (!connection.getTargetId().equals(authenticatedUserId)) {
            throw new ForbiddenException("You are not authorized to reject this connection");
        }

        if (connection.getStatus() != ConnectionStatus.REQUESTED) {
            throw new BadRequestException("Connection request is not pending");
        }

        connection.setStatus(ConnectionStatus.REJECTED);
        Connection updated = connectionRepository.save(connection);
        return mapToResponse(updated);
    }

    public void deleteConnectionWithUser(Long authenticatedUserId, Long otherUserId) {
        Connection connection = connectionRepository.findAll().stream()
                .filter(c -> c.getStatus() == ConnectionStatus.ACCEPTED)
                .filter(c -> (c.getRequesterId().equals(authenticatedUserId) && c.getTargetId().equals(otherUserId))
                        || (c.getRequesterId().equals(otherUserId) && c.getTargetId().equals(authenticatedUserId)))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Connection not found"));

        // Clean up chats between the users and clear unread counts
        ChatRoom room = chatRoomRepository.findByUserAAndUserB(authenticatedUserId, otherUserId);
        if (room == null) {
            room = chatRoomRepository.findByUserAAndUserB(otherUserId, authenticatedUserId);
        }
        if (room != null) {
            chatMessageRepository.markMessagesAsRead(room.getId(), authenticatedUserId);
            chatMessageRepository.markMessagesAsRead(room.getId(), otherUserId);
            chatRoomRepository.delete(room);
        }

        connectionRepository.delete(connection);
    }

    private ConnectionResponse mapToResponse(Connection c) {
        return new ConnectionResponse(c.getId(), c.getRequesterId(), c.getTargetId(), c.getStatus());
    }
}