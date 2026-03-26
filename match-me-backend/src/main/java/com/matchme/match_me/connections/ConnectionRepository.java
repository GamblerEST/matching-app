package com.matchme.match_me.connections;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    List<Connection> findByTargetIdAndStatus(Long targetId, ConnectionStatus status);

    List<Connection> findByRequesterIdAndStatus(Long requesterId, ConnectionStatus status);

    boolean existsByRequesterIdAndTargetId(Long requesterId, Long targetId);

    boolean existsByRequesterIdAndTargetIdAndStatus(Long requesterId, Long targetId, ConnectionStatus status);
}
