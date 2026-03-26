package com.matchme.match_me.permissions;

import org.springframework.stereotype.Service;
import com.matchme.match_me.common.exception.ForbiddenException;
import com.matchme.match_me.connections.ConnectionRepository;
import com.matchme.match_me.recommendations.RecommendationService;

@Service
public class PermissionService {

    private final ConnectionRepository connectionRepository;
    private final RecommendationService recommendationService;

    public PermissionService(ConnectionRepository connectionRepository, 
                           RecommendationService recommendationService) {
        this.connectionRepository = connectionRepository;
        this.recommendationService = recommendationService;
    }

    /**
     * Check if the authenticated user can view the target user's profile
     */
    public boolean canViewProfile(Long authenticatedUserId, Long targetUserId) {
        // Can always view own profile
        if (authenticatedUserId.equals(targetUserId)) {
            return true;
        }

        // Check if they have a connection
        boolean hasConnection = connectionRepository.existsByRequesterIdAndTargetId(authenticatedUserId, targetUserId)
                || connectionRepository.existsByRequesterIdAndTargetId(targetUserId, authenticatedUserId);
        
        if (hasConnection) {
            return true;
        }

        // Check if target user is in recommendations
        return recommendationService.isUserRecommended(authenticatedUserId, targetUserId);
    }

    /**
     * Verify user can view profile, throw exception if not
     */
    public void requireViewPermission(Long authenticatedUserId, Long targetUserId) {
        if (!canViewProfile(authenticatedUserId, targetUserId)) {
            throw new ForbiddenException("You do not have permission to view this profile");
        }
    }

    /**
     * Verify user can only modify their own data
     */
    public void requireOwnership(Long authenticatedUserId, Long resourceOwnerId) {
        if (!authenticatedUserId.equals(resourceOwnerId)) {
            throw new ForbiddenException("You can only modify your own data");
        }
    }
}