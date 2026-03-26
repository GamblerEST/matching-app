package com.matchme.match_me.chat;

import java.security.Principal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.matchme.match_me.chat.dto.ChatMessageResponse;
import com.matchme.match_me.chat.dto.ChatRoomResponse;
import com.matchme.match_me.chat.dto.ChatSummaryResponse;
import com.matchme.match_me.chat.dto.CreateChatMessageRequest;
import com.matchme.match_me.chat.dto.SendMessageRequest;
import com.matchme.match_me.common.exception.UnauthorizedException;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/room")
        public ResponseEntity<ChatRoomResponse> createRoom(@RequestParam Long otherUserId) {
            Long authenticatedUserId = getAuthenticatedUserId();
            ChatRoomResponse response = chatService.createChatRoom(authenticatedUserId, otherUserId);
            return ResponseEntity.ok(response);
        }

    @PostMapping("/message")
    public ResponseEntity<ChatMessageResponse> sendMessage(@RequestBody CreateChatMessageRequest request) {
        Long authenticatedUserId = getAuthenticatedUserId();
        ChatMessageResponse response = chatService.sendMessage(request, authenticatedUserId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/messages/{chatRoomId}")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(@PathVariable Long chatRoomId) {
        Long authenticatedUserId = getAuthenticatedUserId();
        List<ChatMessageResponse> messages = chatService.getMessages(chatRoomId, authenticatedUserId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/chats")
    public ResponseEntity<List<ChatSummaryResponse>> getAllChats() {
        Long userId = getAuthenticatedUserId();
        List<ChatSummaryResponse> chats = chatService.getAllChatsForUser(userId);
        return ResponseEntity.ok(chats);
    }

    @GetMapping("/chats/{chatId}/messages")
    public ResponseEntity<Page<ChatMessageResponse>> getPaginatedMessages(
            @PathVariable Long chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long authenticatedUserId = getAuthenticatedUserId();
        Page<ChatMessageResponse> messages = chatService.getMessagesPaginated(chatId, page, size, authenticatedUserId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/chats/unread-count")
    public ResponseEntity<Long> getUnreadCount() {
        Long userId = getAuthenticatedUserId();
        long unreadCount = chatService.getUnreadCountForUser(userId);
        return ResponseEntity.ok(unreadCount);
    }

    @PutMapping("/chats/{chatId}/mark-read")
    public ResponseEntity<Void> markMessagesAsRead(@PathVariable Long chatId) {
        Long userId = getAuthenticatedUserId();
        chatService.markMessagesAsRead(chatId, userId);
        return ResponseEntity.noContent().build();
    }

    @MessageMapping("/chat.send")
    public void sendMessageViaWebSocket(@Payload SendMessageRequest request, Principal principal) {
        Long senderId = Long.parseLong(principal.getName());
        ChatRoom room = chatService.getChatRoomById(request.chatRoomId());
        Long receiverId = room.getUserA().equals(senderId) ? room.getUserB() : room.getUserA();

        CreateChatMessageRequest messageRequest = new CreateChatMessageRequest(
                request.chatRoomId(),
                senderId,
                receiverId,
                request.message());

        ChatMessageResponse response = chatService.sendMessage(messageRequest, senderId);

        messagingTemplate.convertAndSendToUser(
                String.valueOf(response.senderId()),
                "/queue/messages",
                response);

        messagingTemplate.convertAndSendToUser(
                String.valueOf(response.receiverId()),
                "/queue/messages",
                response);
    }

    @MessageMapping("/chat.typing")
    public void handleTypingIndicator(@Payload SendMessageRequest request, Principal principal) {
        Long senderId = Long.parseLong(principal.getName());
        ChatRoom room = chatService.getChatRoomById(request.chatRoomId());
        Long receiverId = room.getUserA().equals(senderId) ? room.getUserB() : room.getUserA();

        messagingTemplate.convertAndSendToUser(
                String.valueOf(receiverId),
                "/queue/typing",
                new TypingIndicator(request.chatRoomId(), senderId, true));
    }

    private record TypingIndicator(Long chatRoomId, Long userId, boolean isTyping) {}

    private Long getAuthenticatedUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new UnauthorizedException("Authentication required");
        }
        return (Long) auth.getPrincipal();
    }
}