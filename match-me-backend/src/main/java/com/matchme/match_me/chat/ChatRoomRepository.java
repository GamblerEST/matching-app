package com.matchme.match_me.chat;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // find by exact pair (either order may be needed; provide both helpers)
    ChatRoom findByUserAAndUserB(Long userA, Long userB);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.userA = :userId OR cr.userB = :userId")
    List<ChatRoom> findChatRoomsByUserId(Long userId);

}