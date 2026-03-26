package com.matchme.match_me.recommendations;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DismissedRecommendationRepository extends JpaRepository<DismissedRecommendation, Long> {

    boolean existsByUserIdAndDismissedUserId(Long userId, Long dismissedUserId);

    @Query("SELECT dr.dismissedUserId FROM DismissedRecommendation dr WHERE dr.userId = :userId")
    List<Long> findDismissedUserIdsByUserId(Long userId);
}
