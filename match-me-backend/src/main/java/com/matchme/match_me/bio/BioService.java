package com.matchme.match_me.bio;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.matchme.match_me.bio.dto.BioResponse;
import com.matchme.match_me.bio.dto.UpdateBioRequest;
import com.matchme.match_me.common.exception.NotFoundException;
import com.matchme.match_me.permissions.PermissionService;
@Service
@Transactional
public class BioService {

    private final BioRepository bioRepository;
    private final PermissionService permissionService;

    public BioService(BioRepository bioRepository,
                     PermissionService permissionService) {
        this.bioRepository = bioRepository;
        this.permissionService = permissionService;
    }

    public BioResponse updateBio(Long userId, UpdateBioRequest request, Long authenticatedUserId) {
        // Authorization: User can only update their own bio
        permissionService.requireOwnership(authenticatedUserId, userId);

        Bio bio = bioRepository.findByUserId(userId);
        if (bio == null) {
            throw new NotFoundException("Bio not found for user id: " + userId);
        }

        bio.setHobbies(request.hobbies());
        bio.setInterests(request.interests());
        bio.setFoodPreferences(request.foodPreferences());
        bio.setMusicTaste(request.musicTaste());
        bio.setPersonalityType(request.personalityType());
        bio.setLookingFor(request.lookingFor());

        Bio updatedBio = bioRepository.save(bio);
        return mapToResponse(updatedBio);
    }

    public BioResponse getBioByUserId(Long userId, Long authenticatedUserId) {
        // Authorization: Check if user has permission to view this bio
        permissionService.requireViewPermission(authenticatedUserId, userId);

        Bio bio = bioRepository.findByUserId(userId);
        if (bio == null) {
            throw new NotFoundException("Bio not found for user id: " + userId);
        }
        return mapToResponse(bio);
    }

    private BioResponse mapToResponse(Bio bio) {
        return new BioResponse(
                bio.getId(),
                bio.getUser().getId(),
                bio.getHobbies(),
                bio.getInterests(),
                bio.getFoodPreferences(),
                bio.getMusicTaste(),
                bio.getPersonalityType(),
                bio.getLookingFor()
        );
    }
}