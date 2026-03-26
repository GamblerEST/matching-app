package com.matchme.match_me.recommendations;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.matchme.match_me.common.exception.UnauthorizedException;
import com.matchme.match_me.recommendations.dto.DismissRecommendationResponse;
import com.matchme.match_me.recommendations.dto.RecommendationResponse;
import com.matchme.match_me.users.ProfileCompletionService;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final ProfileCompletionService profileCompletionService;

    public RecommendationController(RecommendationService recommendationService,
                                   ProfileCompletionService profileCompletionService) {
        this.recommendationService = recommendationService;
        this.profileCompletionService = profileCompletionService;
    }
    
    @GetMapping
    public ResponseEntity<List<RecommendationResponse>> getRecommendations() {
        Long userId = getAuthenticatedUserId();
        
        // Service will throw ProfileIncompleteException if needed
        profileCompletionService.requireCompleteProfile(userId);
        
        List<RecommendationResponse> recommendations = recommendationService.getRecommendations(userId, 10);
        return ResponseEntity.ok(recommendations);
    }

    @PostMapping("/{userId}/dismiss")
    public ResponseEntity<DismissRecommendationResponse> dismissRecommendation(@PathVariable Long userId) {
        Long authenticatedUserId = getAuthenticatedUserId();
        
        recommendationService.dismissRecommendation(authenticatedUserId, userId);
        return ResponseEntity.ok(new DismissRecommendationResponse(
            "Recommendation dismissed successfully", 
            userId
        ));
    }

    private Long getAuthenticatedUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new UnauthorizedException("Authentication required");
        }
        return (Long) auth.getPrincipal();
    }
}