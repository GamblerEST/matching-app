package com.matchme.match_me.chat;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoomIdOrderByTimestampAsc(Long chatRoomId);

    Page<ChatMessage> findByChatRoomIdOrderByTimestampDesc(Long chatRoomId, Pageable pageable);

    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.chatRoomId = :chatRoomId AND m.senderId != :userId AND m.seen = false")
    long countUnreadMessagesInChatRoom(Long chatRoomId, Long userId);

    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.chatRoomId IN :chatRoomIds AND m.senderId != :userId AND m.seen = false")
    long countUnreadMessagesForUser(List<Long> chatRoomIds, Long userId);

    @Modifying
    @Query("UPDATE ChatMessage m SET m.seen = true WHERE m.chatRoomId = :chatRoomId AND m.senderId != :userId")
    void markMessagesAsRead(Long chatRoomId, Long userId);

    ChatMessage findFirstByChatRoomIdOrderByTimestampDesc(Long chatRoomId);
}