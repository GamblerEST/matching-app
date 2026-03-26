package com.matchme.match_me.chat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.matchme.match_me.chat.dto.ChatMessageResponse;
import com.matchme.match_me.chat.dto.ChatRoomResponse;
import com.matchme.match_me.chat.dto.ChatSummaryResponse;
import com.matchme.match_me.chat.dto.CreateChatMessageRequest;
import com.matchme.match_me.common.exception.BadRequestException;
import com.matchme.match_me.common.exception.ForbiddenException;
import com.matchme.match_me.common.exception.NotFoundException;
import com.matchme.match_me.connections.ConnectionRepository;
import com.matchme.match_me.connections.ConnectionStatus;

@Service
@Transactional
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ConnectionRepository connectionRepository;

    public ChatService(ChatRoomRepository chatRoomRepository,
                       ChatMessageRepository chatMessageRepository, ConnectionRepository connectionRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.connectionRepository = connectionRepository;
    }

    //requires connection and uses authenticatedUserId as userA
        public ChatRoomResponse createChatRoom(Long authenticatedUserId, Long otherUserId) {
            if (authenticatedUserId.equals(otherUserId)) {
                throw new BadRequestException("Cannot create chat room with yourself");
            }
    
            //Verify users are connected before allowing chat room creation
            boolean isConnected = connectionRepository.existsByRequesterIdAndTargetIdAndStatus(
                authenticatedUserId, otherUserId, ConnectionStatus.ACCEPTED) ||
                connectionRepository.existsByRequesterIdAndTargetIdAndStatus(
                otherUserId, authenticatedUserId, ConnectionStatus.ACCEPTED);
    
            if (!isConnected) {
                throw new ForbiddenException("You can only create chat rooms with connected users");
            }


            // Check both orders (A,B) or (B,A)
                ChatRoom existingRoom = chatRoomRepository.findByUserAAndUserB(authenticatedUserId, otherUserId);
                if (existingRoom == null) {
                    existingRoom = chatRoomRepository.findByUserAAndUserB(otherUserId, authenticatedUserId);
                }
        
                if (existingRoom != null) {
                    return mapToChatRoomResponse(existingRoom);
                }
        
                ChatRoom room = new ChatRoom();
                room.setUserA(authenticatedUserId);
                room.setUserB(otherUserId);
                chatRoomRepository.save(room);
        
                return mapToChatRoomResponse(room);
            }

    public ChatMessageResponse sendMessage(CreateChatMessageRequest request, Long authenticatedUserId) {
        ChatRoom room = chatRoomRepository.findById(request.chatRoomId())
                .orElseThrow(() -> new NotFoundException("Chat room not found"));

        // Ensure underlying connection is still active
        requireActiveConnection(room);

        // Verify sender is authenticated user
        if (!request.senderId().equals(authenticatedUserId)) {
            throw new ForbiddenException("Cannot send messages as another user");
        }

        // Verify sender is participant in the chat room
        if (!room.getUserA().equals(request.senderId()) && !room.getUserB().equals(request.senderId())) {
            throw new ForbiddenException("You are not a participant in this chat room");
        }

        Long receiverId = resolveReceiver(room, request.senderId());

        ChatMessage message = new ChatMessage();
        message.setChatRoomId(room.getId());
        message.setSenderId(request.senderId());
        message.setMessage(request.message());
        message.setSeen(false);

        chatMessageRepository.save(message);

        return mapToChatMessageResponse(message, receiverId);
    }

    public List<ChatMessageResponse> getMessages(Long chatRoomId, Long authenticatedUserId) {
        ChatRoom room = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NotFoundException("Chat room not found"));

        requireActiveConnection(room);

        // Verify user is participant
        if (!room.getUserA().equals(authenticatedUserId) && !room.getUserB().equals(authenticatedUserId)) {
            throw new ForbiddenException("You are not a participant in this chat room");
        }

        return chatMessageRepository
                .findByChatRoomIdOrderByTimestampAsc(chatRoomId)
                .stream()
                .map(msg -> mapToChatMessageResponse(msg, resolveReceiver(room, msg.getSenderId())))
                .collect(Collectors.toList());
    }

    public ChatRoom getChatRoomById(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NotFoundException("Chat room not found"));
    }

    public Page<ChatMessageResponse> getMessagesPaginated(Long chatRoomId, int page, int size, Long authenticatedUserId) {
        ChatRoom room = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NotFoundException("Chat room not found"));

        requireActiveConnection(room);

        // Verify user is participant
        if (!room.getUserA().equals(authenticatedUserId) && !room.getUserB().equals(authenticatedUserId)) {
            throw new ForbiddenException("You are not a participant in this chat room");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessage> messagesPage = chatMessageRepository
                .findByChatRoomIdOrderByTimestampDesc(chatRoomId, pageable);

        return messagesPage.map(msg -> mapToChatMessageResponse(msg, resolveReceiver(room, msg.getSenderId())));
    }

    public List<ChatSummaryResponse> getAllChatsForUser(Long userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsByUserId(userId)
            .stream()
            .filter(this::isConnectionActive)
            .collect(Collectors.toList());
        List<ChatSummaryResponse> chatSummaries = new ArrayList<>();

        for (ChatRoom room : chatRooms) {
            ChatMessage lastMessage = chatMessageRepository.findFirstByChatRoomIdOrderByTimestampDesc(room.getId());
            
            if (lastMessage == null) {
                continue;
            }

            Long otherUserId = room.getUserA().equals(userId) ? room.getUserB() : room.getUserA();
            long unreadCount = chatMessageRepository.countUnreadMessagesInChatRoom(room.getId(), userId);

            chatSummaries.add(new ChatSummaryResponse(
                room.getId(),
                otherUserId,
                lastMessage.getMessage(),
                lastMessage.getTimestamp(),
                unreadCount
            ));
        }

        chatSummaries.sort(Comparator.comparing(ChatSummaryResponse::lastMessageTime).reversed());
        return chatSummaries;
    }

    public long getUnreadCountForUser(Long userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsByUserId(userId)
            .stream()
            .filter(this::isConnectionActive)
            .collect(Collectors.toList());
        List<Long> chatRoomIds = chatRooms.stream()
                .map(ChatRoom::getId)
                .collect(Collectors.toList());

        if (chatRoomIds.isEmpty()) {
            return 0;
        }

        return chatMessageRepository.countUnreadMessagesForUser(chatRoomIds, userId);
    }

    public void markMessagesAsRead(Long chatRoomId, Long authenticatedUserId) {
        ChatRoom room = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NotFoundException("Chat room not found"));

        requireActiveConnection(room);

        // Verify user is part of the chat room
        if (!room.getUserA().equals(authenticatedUserId) && !room.getUserB().equals(authenticatedUserId)) {
            throw new ForbiddenException("You are not a participant in this chat room");
        }

        chatMessageRepository.markMessagesAsRead(chatRoomId, authenticatedUserId);
    }

    private Long resolveReceiver(ChatRoom room, Long senderId) {
        return (room.getUserA().equals(senderId))
                ? room.getUserB()
                : room.getUserA();
    }

    private ChatRoomResponse mapToChatRoomResponse(ChatRoom room) {
        return new ChatRoomResponse(
                room.getId(),
                room.getUserA(),
                room.getUserB()
        );
    }

    private ChatMessageResponse mapToChatMessageResponse(ChatMessage message, Long receiverId) {
        return new ChatMessageResponse(
                message.getId(),
                message.getChatRoomId(),
                message.getSenderId(),
                receiverId,
                message.getMessage(),
                message.isSeen(),
                message.getTimestamp()
        );
    }

    private boolean isConnectionActive(ChatRoom room) {
        Long a = room.getUserA();
        Long b = room.getUserB();
        return connectionRepository.existsByRequesterIdAndTargetIdAndStatus(a, b, ConnectionStatus.ACCEPTED)
                || connectionRepository.existsByRequesterIdAndTargetIdAndStatus(b, a, ConnectionStatus.ACCEPTED);
    }

    private void requireActiveConnection(ChatRoom room) {
        if (!isConnectionActive(room)) {
            throw new ForbiddenException("Users are no longer connected");
        }
    }
}