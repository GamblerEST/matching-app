package com.matchme.match_me.profile;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.matchme.match_me.common.exception.NotFoundException;
import com.matchme.match_me.permissions.PermissionService;
import com.matchme.match_me.profile.dto.ProfileResponse;
import com.matchme.match_me.profile.dto.UpdateProfileRequest;

@Service
@Transactional
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final PermissionService permissionService;

    public ProfileService(ProfileRepository profileRepository,
                         PermissionService permissionService) {
        this.profileRepository = profileRepository;
        this.permissionService = permissionService;
    }

    public ProfileResponse updateProfile(Long userId, UpdateProfileRequest request, Long authenticatedUserId) {
        // Authorization: User can only update their own profile
        permissionService.requireOwnership(authenticatedUserId, userId);

        Profile profile = profileRepository.findByUserId(userId);
        if (profile == null) {
            throw new NotFoundException("Profile not found for userId: " + userId);
        }

        profile.setDisplayName(request.displayName());
        profile.setAboutMe(request.aboutMe());
        profile.setAvatarUrl(request.avatarUrl());
        profile.setComplete(request.complete());

        Profile updated = profileRepository.save(profile);
        return mapToResponse(updated);
    }

    public ProfileResponse getProfileByUserId(Long userId, Long authenticatedUserId) {
        // Authorization: Check if user has permission to view this profile
        permissionService.requireViewPermission(authenticatedUserId, userId);

        Profile profile = profileRepository.findByUserId(userId);
        if (profile == null) {
            throw new NotFoundException("Profile not found for userId: " + userId);
        }

        return mapToResponse(profile);
    }

    public ProfileResponse updateProfilePicture(Long userId, String avatarUrl, Long authenticatedUserId) {
        // Authorization: User can only update their own profile picture
        permissionService.requireOwnership(authenticatedUserId, userId);

        Profile profile = profileRepository.findByUserId(userId);
        if (profile == null) {
            throw new NotFoundException("Profile not found for userId: " + userId);
        }

        profile.setAvatarUrl(avatarUrl);
        Profile updated = profileRepository.save(profile);
        return mapToResponse(updated);
    }

    private ProfileResponse mapToResponse(Profile profile) {
        return new ProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getDisplayName(),
                profile.getAboutMe(),
                profile.getAvatarUrl()
        );
    }
}
