package com.matchme.match_me.bio;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.matchme.match_me.bio.dto.BioResponse;
import com.matchme.match_me.bio.dto.UpdateBioRequest;
import com.matchme.match_me.common.exception.UnauthorizedException;

@RestController
@RequestMapping("/bio")
public class BioController {

    private final BioService bioService;

    public BioController(BioService bioService) {
        this.bioService = bioService;
    }

    @PutMapping("/{userId}")
    public ResponseEntity<BioResponse> updateBio(
            @PathVariable Long userId,
            @RequestBody UpdateBioRequest request) {
        Long authenticatedUserId = getAuthenticatedUserId();
        BioResponse response = bioService.updateBio(userId, request, authenticatedUserId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<BioResponse> getBio(@PathVariable Long userId) {
        Long authenticatedUserId = getAuthenticatedUserId();
        BioResponse response = bioService.getBioByUserId(userId, authenticatedUserId);
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