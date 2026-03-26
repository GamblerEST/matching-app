package com.matchme.match_me.users;

import org.springframework.stereotype.Service;

import com.matchme.match_me.bio.Bio;
import com.matchme.match_me.common.exception.ProfileIncompleteException;
import com.matchme.match_me.location.Location;
import com.matchme.match_me.profile.Profile;

@Service
public class ProfileCompletionService {

    private final UserRepository userRepository;

    public ProfileCompletionService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Check if user has completed their profile
     * Requirements:
     * - Profile exists with displayName and avatarUrl
     * - Bio exists with all required fields (hobbies, interests, foodPreferences, musicTaste, personalityType, lookingFor)
     * - Location exists with coordinates and maxRadius
     */
    public boolean isProfileComplete(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }

        Profile profile = user.getProfile();
        Bio bio = user.getBio();
        Location location = user.getLocation();

        // Check profile
        if (profile == null || profile.getDisplayName() == null || profile.getDisplayName().trim().isEmpty() || profile.getAboutMe() == null || profile.getAboutMe().trim().isEmpty()) {
            return false;
        }

        // Check bio - all fields required (6 data points)
        if (bio == null ||
            bio.getHobbies() == null || bio.getHobbies().trim().isEmpty() ||
            bio.getInterests() == null || bio.getInterests().trim().isEmpty() ||
            bio.getFoodPreferences() == null || bio.getFoodPreferences().trim().isEmpty() ||
            bio.getMusicTaste() == null || bio.getMusicTaste().trim().isEmpty() ||
            bio.getPersonalityType() == null || bio.getPersonalityType().trim().isEmpty() ||
            bio.getLookingFor() == null || bio.getLookingFor().trim().isEmpty()) {
            return false;
        }

        // Check location
        if (location == null ||
            location.getLatitude() == null || location.getLatitude() == 0.0 ||
            location.getLongitude() == null || location.getLongitude() == 0.0 ||
            location.getMaxRadiusKm() == null || location.getMaxRadiusKm() == 0.0 ||
            location.getCity() == null) {
            return false;
        }

        return true;
    }

    /**
     * Verify profile is complete, throw exception if not
     */
    public void requireCompleteProfile(Long userId) {
        if (!isProfileComplete(userId)) {
            throw new ProfileIncompleteException(
                "Profile must be completed before accessing this feature. " +
                "Please complete your profile, bio (hobbies, interests, food preferences, " +
                "music taste, personality type, looking for), and location with coordinates."
            );
        }
    }
}