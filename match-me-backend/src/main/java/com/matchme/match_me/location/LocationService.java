package com.matchme.match_me.location;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.matchme.match_me.common.exception.NotFoundException;
import com.matchme.match_me.location.dto.LocationResponse;
import com.matchme.match_me.location.dto.UpdateLocationRequest;
import com.matchme.match_me.permissions.PermissionService;

@Service
@Transactional
public class LocationService {

    private final LocationRepository locationRepository;
    private final PermissionService permissionService;

    public LocationService(LocationRepository locationRepository,
                          PermissionService permissionService) {
        this.locationRepository = locationRepository;
        this.permissionService = permissionService;
    }

    // ✅ FIXED: No userId parameter
    public LocationResponse updateLocation(UpdateLocationRequest request, Long authenticatedUserId) {
        Location location = locationRepository.findByUserId(authenticatedUserId);
        if (location == null) {
            throw new NotFoundException("Location not found");
        }

        location.setLatitude(request.latitude());
        location.setLongitude(request.longitude());
        location.setMaxRadiusKm(request.maxRadiusKm());
        location.setCity(request.city());

        Location updated = locationRepository.save(location);
        return mapToResponse(updated);
    }

    public LocationResponse getLocationByUserId(Long userId, Long authenticatedUserId) {
        permissionService.requireViewPermission(authenticatedUserId, userId);

        Location location = locationRepository.findByUserId(userId);
        if (location == null) {
            throw new NotFoundException("Location not found for userId: " + userId);
        }

        return mapToResponse(location);
    }

    private LocationResponse mapToResponse(Location location) {
        return new LocationResponse(
                location.getId(),
                location.getUser().getId(),
                location.getLatitude(),
                location.getLongitude(),
                location.getMaxRadiusKm(),
                location.getCity()
        );
    }
}
