package com.matchme.match_me.location;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.matchme.match_me.common.exception.UnauthorizedException;
import com.matchme.match_me.location.dto.LocationResponse;
import com.matchme.match_me.location.dto.UpdateLocationRequest;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    // ✅ FIXED: PUT /locations (no userId in URL)
    @PutMapping
    public ResponseEntity<LocationResponse> updateLocation(@RequestBody UpdateLocationRequest request) {
        Long authenticatedUserId = getAuthenticatedUserId();
        LocationResponse response = locationService.updateLocation(request, authenticatedUserId);
        return ResponseEntity.ok(response);
    }

    // ✅ KEPT: GET /locations/{userId} (view other user's location with permission check)
    @GetMapping("/{userId}")
    public ResponseEntity<LocationResponse> getLocation(@PathVariable Long userId) {
        Long authenticatedUserId = getAuthenticatedUserId();
        LocationResponse response = locationService.getLocationByUserId(userId, authenticatedUserId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<LocationResponse> getLocation() {
        Long authenticatedUserId = getAuthenticatedUserId();
        LocationResponse response = locationService.getLocationByUserId(authenticatedUserId, authenticatedUserId);
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