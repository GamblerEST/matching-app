package com.matchme.match_me.profile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.matchme.match_me.common.exception.BadRequestException;
import com.matchme.match_me.common.exception.NotFoundException;
import com.matchme.match_me.common.exception.UnauthorizedException;
import com.matchme.match_me.profile.dto.ProfileResponse;

@RestController
@RequestMapping
public class ImageController {

    private final ImageService imageService;
    private final ProfileService profileService;

    public ImageController(ImageService imageService, ProfileService profileService) {
        this.imageService = imageService;
        this.profileService = profileService;
    }

    @PostMapping("/me/profile/picture")
    public ResponseEntity<ProfileResponse> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        Long userId = getAuthenticatedUserId();

        try {
            String imageUrl = imageService.uploadProfilePicture(file);
            ProfileResponse profile = profileService.updateProfilePicture(userId, imageUrl, userId);
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage());
        } catch (IOException e) {
            throw new BadRequestException("Failed to upload image");
        }
    }

    @DeleteMapping("/me/profile/picture")
    public ResponseEntity<ProfileResponse> deleteProfilePicture() {
        Long userId = getAuthenticatedUserId();

        ProfileResponse profile = profileService.getProfileByUserId(userId, userId);
        
        if (profile.avatarUrl() != null) {
            try {
                imageService.deleteProfilePicture(profile.avatarUrl());
            } catch (IOException e) {
                throw new BadRequestException("Failed to delete image");
            }
        }
        
        ProfileResponse updatedProfile = profileService.updateProfilePicture(userId, null, userId);
        return ResponseEntity.ok(updatedProfile);
    }

    @GetMapping("/api/images/{filename}")
    public ResponseEntity<Resource> serveImage(@PathVariable String filename) {
        try {
            Path imagePath = imageService.getImagePath(filename);
            
            if (!Files.exists(imagePath)) {
                throw new NotFoundException("Image not found");
            }

            byte[] imageBytes = Files.readAllBytes(imagePath);
            ByteArrayResource resource = new ByteArrayResource(imageBytes);

            String contentType = Files.probeContentType(imagePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage());
        } catch (IOException e) {
            throw new BadRequestException("Failed to load image");
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