package com.matchme.match_me.users;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.matchme.match_me.bio.BioService;
import com.matchme.match_me.bio.dto.BioResponse;
import com.matchme.match_me.common.exception.NotFoundException;
import com.matchme.match_me.common.exception.UnauthorizedException;
import com.matchme.match_me.permissions.PermissionService;
import com.matchme.match_me.profile.ProfileService;
import com.matchme.match_me.profile.dto.ProfileResponse;
import com.matchme.match_me.users.dto.CreateUserRequest;
import com.matchme.match_me.users.dto.ProfileCompletionResponse;
import com.matchme.match_me.users.dto.UpdateUserRequest;
import com.matchme.match_me.users.dto.UserResponse;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ProfileService profileService;
    private final BioService bioService;
    private final PermissionService permissionService;
    private final ProfileCompletionService profileCompletionService;

    public UserController(UserService userService,
                         ProfileService profileService,
                         BioService bioService,
                         PermissionService permissionService,
                         ProfileCompletionService profileCompletionService) {
        this.userService = userService;
        this.profileService = profileService;
        this.bioService = bioService;
        this.permissionService = permissionService;
        this.profileCompletionService = profileCompletionService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable("id") Long id,
            @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("id") Long id) {
        Long authenticatedUserId = getAuthenticatedUserId();
        
        if (!userService.userExists(id)) {
            throw new NotFoundException("User not found");
        }
        
        // Permission check handled by service - will throw ForbiddenException if not allowed
        permissionService.requireViewPermission(authenticatedUserId, id);
        
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<ProfileResponse> getUserProfile(@PathVariable("id") Long id) {
        Long authenticatedUserId = getAuthenticatedUserId();
        
        if (!userService.userExists(id)) {
            throw new NotFoundException("User not found");
        }
        
        // Permission check and retrieval handled by service
        ProfileResponse response = profileService.getProfileByUserId(id, authenticatedUserId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/bio")
    public ResponseEntity<BioResponse> getUserBio(@PathVariable("id") Long id) {
        Long authenticatedUserId = getAuthenticatedUserId();
        
        if (!userService.userExists(id)) {
            throw new NotFoundException("User not found");
        }
        
        // Permission check and retrieval handled by service
        BioResponse response = bioService.getBioByUserId(id, authenticatedUserId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe() {
        Long userId = getAuthenticatedUserId();
        UserResponse response = userService.getUserById(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/profile")
    public ResponseEntity<ProfileResponse> getMeProfile() {
        Long userId = getAuthenticatedUserId();
        ProfileResponse response = profileService.getProfileByUserId(userId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/bio")
    public ResponseEntity<BioResponse> getMeBio() {
        Long userId = getAuthenticatedUserId();
        BioResponse response = bioService.getBioByUserId(userId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/profile-completion")
    public ResponseEntity<ProfileCompletionResponse> getProfileCompletionStatus() {
        Long userId = getAuthenticatedUserId();
        boolean isComplete = profileCompletionService.isProfileComplete(userId);
        
        if (isComplete) {
            return ResponseEntity.ok(new ProfileCompletionResponse(
                true,
                "Profile is complete",
                new ProfileCompletionResponse.MissingFields(false, false, false)
            ));
        } else {
            User user = userService.getUserEntityById(userId);
            boolean profileMissing = user.getProfile() == null || 
                                    user.getProfile().getDisplayName() == null || 
                                    user.getProfile().getDisplayName().trim().isEmpty();
            boolean bioMissing = user.getBio() == null || 
                                user.getBio().getHobbies() == null || 
                                user.getBio().getInterests() == null;
            boolean locationMissing = user.getLocation() == null || 
                                     user.getLocation().getLatitude() == null;
            
            return ResponseEntity.ok(new ProfileCompletionResponse(
                false,
                "Profile is incomplete. Please complete all required fields.",
                new ProfileCompletionResponse.MissingFields(profileMissing, bioMissing, locationMissing)
            ));
        }
    }

    private Long getAuthenticatedUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new UnauthorizedException("Authentication required");
        }
        return (Long) auth.getPrincipal();
    }
}