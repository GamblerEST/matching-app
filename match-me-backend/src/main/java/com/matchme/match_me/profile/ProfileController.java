package com.matchme.match_me.profile;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.matchme.match_me.common.exception.UnauthorizedException;
import com.matchme.match_me.profile.dto.ProfileResponse;
import com.matchme.match_me.profile.dto.UpdateProfileRequest;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ProfileResponse> updateProfile(
            @PathVariable Long userId,
            @RequestBody UpdateProfileRequest request) {
        Long authenticatedUserId = getAuthenticatedUserId();
        ProfileResponse response = profileService.updateProfile(userId, request, authenticatedUserId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable Long userId) {
        Long authenticatedUserId = getAuthenticatedUserId();
        ProfileResponse response = profileService.getProfileByUserId(userId, authenticatedUserId);
        return ResponseEntity.ok(response);
    }

    private Long getAuthenticatedUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new UnauthorizedException("Authentication required");
        }
        return (Long) auth.getPrincipal();
    }
}