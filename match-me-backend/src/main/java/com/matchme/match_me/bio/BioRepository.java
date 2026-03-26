package com.matchme.match_me.bio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BioRepository extends JpaRepository<Bio, Long> {

    Bio findByUserId(Long userId);
}