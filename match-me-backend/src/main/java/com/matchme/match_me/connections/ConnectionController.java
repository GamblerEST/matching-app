package com.matchme.match_me.connections;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.matchme.match_me.common.exception.UnauthorizedException;
import com.matchme.match_me.connections.dto.ConnectionIdResponse;
import com.matchme.match_me.connections.dto.ConnectionResponse;
import com.matchme.match_me.connections.dto.CreateConnectionRequest;
import com.matchme.match_me.users.ProfileCompletionService;

@RestController
@RequestMapping("/connections")
public class ConnectionController {

    private final ConnectionService connectionService;
    private final ProfileCompletionService profileCompletionService;

    public ConnectionController(ConnectionService connectionService,
                               ProfileCompletionService profileCompletionService) {
        this.connectionService = connectionService;
        this.profileCompletionService = profileCompletionService;
    }

    @GetMapping
    public ResponseEntity<List<ConnectionIdResponse>> getConnections() {
        Long userId = getAuthenticatedUserId();
        List<ConnectionResponse> connections = connectionService.getConnectionsForUser(userId);
        
        List<ConnectionIdResponse> idResponses = connections.stream()
                .map(conn -> new ConnectionIdResponse(
                    conn.requesterId().equals(userId) ? conn.targetId() : conn.requesterId()
                ))
                .toList();
        
        return ResponseEntity.ok(idResponses);
    }

    @GetMapping("/requests")
    public ResponseEntity<List<ConnectionResponse>> getConnectionRequests() {
        Long userId = getAuthenticatedUserId();
        List<ConnectionResponse> requests = connectionService.getPendingRequestsForUser(userId);
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/request/{userId}")
    public ResponseEntity<ConnectionResponse> requestConnection(@PathVariable Long userId) {
        Long authenticatedUserId = getAuthenticatedUserId();
        
        // Require complete profile before allowing connection requests
        profileCompletionService.requireCompleteProfile(authenticatedUserId);
        
        CreateConnectionRequest request = new CreateConnectionRequest(authenticatedUserId, userId);
        ConnectionResponse response = connectionService.createConnection(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<ConnectionResponse> acceptConnection(@PathVariable Long id) {
        Long userId = getAuthenticatedUserId();
        ConnectionResponse response = connectionService.acceptConnection(id, userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ConnectionResponse> rejectConnection(@PathVariable Long id) {
        Long userId = getAuthenticatedUserId();
        ConnectionResponse response = connectionService.rejectConnection(id, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{otherUserId}")
    public ResponseEntity<Void> deleteConnection(@PathVariable Long otherUserId) {
        Long authenticatedUserId = getAuthenticatedUserId();
        connectionService.deleteConnectionWithUser(authenticatedUserId, otherUserId);
        return ResponseEntity.noContent().build();
    }

    private Long getAuthenticatedUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new UnauthorizedException("Authentication required");
        }
        return (Long) auth.getPrincipal();
    }
}