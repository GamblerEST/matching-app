package com.matchme.match_me.recommendations;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.matchme.match_me.bio.Bio;
import com.matchme.match_me.common.exception.BadRequestException;
import com.matchme.match_me.common.exception.ConflictException;
import com.matchme.match_me.common.exception.NotFoundException;
import com.matchme.match_me.connections.ConnectionRepository;
import com.matchme.match_me.connections.ConnectionStatus;
import com.matchme.match_me.location.DistanceUtil;
import com.matchme.match_me.location.Location;
import com.matchme.match_me.recommendations.dto.RecommendationResponse;
import com.matchme.match_me.users.User;
import com.matchme.match_me.users.UserRepository;

@Service
@Transactional(readOnly = true)
public class RecommendationService {

    private static final double MIN_RECOMMENDATION_SCORE = 3.0;

    private final UserRepository userRepository;
    private final DismissedRecommendationRepository dismissedRecommendationRepository;
    private final ConnectionRepository connectionRepository;

    public RecommendationService(UserRepository userRepository,
            DismissedRecommendationRepository dismissedRecommendationRepository,
            ConnectionRepository connectionRepository) {
        this.userRepository = userRepository;
        this.dismissedRecommendationRepository = dismissedRecommendationRepository;
        this.connectionRepository = connectionRepository;
    }

    public List<RecommendationResponse> getRecommendations(Long userId, int maxResults) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Bio currentBio = currentUser.getBio();
        Location currentLocation = currentUser.getLocation();

        if (currentBio == null
                || currentLocation == null
                || currentLocation.getLatitude() == null
                || currentLocation.getLongitude() == null) {

            throw new BadRequestException("Complete your profile (location required) to get recommendations");
        }

        List<Long> dismissedUserIds = dismissedRecommendationRepository.findDismissedUserIdsByUserId(userId);
        List<Long> connectedUserIds = getConnectedUserIds(userId);

        List<ScoredRecommendation> scoredRecs = new ArrayList<>();

        for (User otherUser : userRepository.findAll()) {

            if (otherUser.getId().equals(userId))
                continue;

            if (dismissedUserIds.contains(otherUser.getId()))
                continue;
            if (connectedUserIds.contains(otherUser.getId()))
                continue;

            Bio otherBio = otherUser.getBio();
            Location otherLocation = otherUser.getLocation();

            if (otherBio == null
                    || otherLocation == null
                    || otherLocation.getLatitude() == null
                    || otherLocation.getLongitude() == null) {
                continue;
            }

            double score = calculateScore(
                    currentBio,
                    otherBio,
                    currentLocation,
                    otherLocation);

            if (score >= MIN_RECOMMENDATION_SCORE) {
                scoredRecs.add(new ScoredRecommendation(otherUser.getId(), score));
            }
        }

        return scoredRecs.stream()
                .sorted(Comparator.comparingDouble(ScoredRecommendation::score).reversed())
                .limit(maxResults)
                .map(sr -> new RecommendationResponse(sr.userId()))
                .toList();
    }

    private double calculateScore(Bio bio1, Bio bio2, Location loc1, Location loc2) {
        double score = 0;

        // 1. Location compatibility - USE DistanceUtil
        double distance = DistanceUtil.calculateDistance(loc1, loc2);

        if (distance <= loc1.getMaxRadiusKm() && distance <= loc2.getMaxRadiusKm()) {
            double maxRadius = Math.min(loc1.getMaxRadiusKm(), loc2.getMaxRadiusKm());
            double proximityScore = 5.0 * (1 - (distance / maxRadius));
            score += proximityScore;
        } else {
            return 0;
        }

        // 2. Looking for compatibility
        if (bio1.getLookingFor() != null && bio2.getLookingFor() != null) {
            String[] looking1 = bio1.getLookingFor().toLowerCase().split(",");
            String[] looking2 = bio2.getLookingFor().toLowerCase().split(",");

            for (String want1 : looking1) {
                for (String want2 : looking2) {
                    if (want1.trim().equals(want2.trim())) {
                        score += 3.0;
                    }
                }
            }
        }

        // 3-6. Other fields
        score += calculateFieldMatchScore(bio1.getHobbies(), bio2.getHobbies(), 2.0);
        score += calculateFieldMatchScore(bio1.getInterests(), bio2.getInterests(), 2.0);
        score += calculateFieldMatchScore(bio1.getFoodPreferences(), bio2.getFoodPreferences(), 1.0);
        score += calculateFieldMatchScore(bio1.getMusicTaste(), bio2.getMusicTaste(), 1.0);

        // 7. Personality type
        if (bio1.getPersonalityType() != null && bio2.getPersonalityType() != null) {
            if (bio1.getPersonalityType().equalsIgnoreCase(bio2.getPersonalityType())) {
                score += 2.5;
            }
        }

        return score;
    }

    private double calculateFieldMatchScore(String field1, String field2, double weightPerMatch) {
        if (field1 == null || field2 == null)
            return 0;

        String[] items1 = field1.toLowerCase().split(",");
        String[] items2 = field2.toLowerCase().split(",");
        double score = 0;

        for (String item1 : items1) {
            for (String item2 : items2) {
                if (item1.trim().equals(item2.trim())) {
                    score += weightPerMatch;
                }
            }
        }

        return score;
    }

    public boolean isUserRecommended(Long authenticatedUserId, Long targetUserId) {
        List<RecommendationResponse> recommendations = getRecommendations(authenticatedUserId, 100);
        return recommendations.stream()
                .anyMatch(rec -> rec.id().equals(targetUserId));
    }

    @Transactional
    public void dismissRecommendation(Long userId, Long dismissedUserId) {
        if (dismissedRecommendationRepository.existsByUserIdAndDismissedUserId(userId, dismissedUserId)) {
            throw new ConflictException("Recommendation already dismissed");
        }

        if (!userRepository.existsById(dismissedUserId)) {
            throw new NotFoundException("User not found");
        }

        DismissedRecommendation dismissed = new DismissedRecommendation();
        dismissed.setUserId(userId);
        dismissed.setDismissedUserId(dismissedUserId);
        dismissedRecommendationRepository.save(dismissed);
    }

    private List<Long> getConnectedUserIds(Long userId) {
        return connectionRepository.findAll().stream()
                .filter(conn -> conn.getStatus() == ConnectionStatus.ACCEPTED)
                .filter(conn -> conn.getRequesterId().equals(userId) || conn.getTargetId().equals(userId))
                .map(conn -> conn.getRequesterId().equals(userId)
                        ? conn.getTargetId()
                        : conn.getRequesterId())
                .toList();
    }

    private record ScoredRecommendation(Long userId, double score) {
    }
}