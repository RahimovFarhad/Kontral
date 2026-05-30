package com.example.Job_Post.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.Job_Post.entity.ChatRoom;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.enumerator.ChatState;
import com.example.Job_Post.repository.ChatRoomRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserService userService;

    public ChatRoom getChatRoomByChatId(String chatId) {
        if (chatId == null || chatId.isEmpty()) {
            throw new IllegalArgumentException("Chat ID must not be null or empty");
        }

        return chatRoomRepository.findByChatId(chatId)
            .orElseThrow(() -> new IllegalStateException("Chat room not found for chat ID: " + chatId));
    }

    public Optional<ChatRoom> getChatRoom(Integer senderId, Integer recipientId, boolean createNewRoomIfNotExists) {
        if (senderId == null || recipientId == null) {
            throw new IllegalArgumentException("Sender ID and Recipient ID must not be null");
        }

        return chatRoomRepository.findByChatId(generateChatId(senderId, recipientId))
            .or(() -> {
                if (createNewRoomIfNotExists) {
                    return Optional.of(createChatRoom(senderId, recipientId));
                }
                return Optional.empty();
            });
    }

    public Optional<ChatRoom> getChatRoom(User sender, User recipient, boolean createNewRoomIfNotExists) {
        if (sender == null || recipient == null) {
            throw new IllegalArgumentException("Sender and Recipient must not be null");
        }

        return chatRoomRepository.findByChatId(generateChatId(sender.getId(), recipient.getId()))
            .or(() -> {
                if (createNewRoomIfNotExists) {
                    return Optional.of(createChatRoom(sender, recipient));
                }
                return Optional.empty();
            });
    }

    private String generateChatId(Integer senderId, Integer recipientId) {
        Integer id1 = Math.max(senderId, recipientId);
        Integer id2 = Math.min(senderId, recipientId);
        return id1 + "_" + id2;
    }

    private ChatRoom createChatRoom(Integer senderId, Integer recipientId) {
        if (senderId == null || recipientId == null) {
            throw new IllegalArgumentException("Sender ID and Recipient ID must not be null");
        }

        if (senderId.equals(recipientId)) {
            throw new IllegalArgumentException("Sender ID and Recipient ID must be different");
        }

        User user1 = userService.getUserById(recipientId);
        User user2 = userService.getUserById(senderId);

        String chatId = generateChatId(senderId, recipientId);

        ChatRoom senderRecipient = ChatRoom.builder()
            .user1(user1)
            .user2(user2)
            .chatId(chatId)
            .chatState(ChatState.REQUEST_PENDING)
            .requestInitiatorId(senderId)
            .build();

        return chatRoomRepository.save(senderRecipient);
    }

    private ChatRoom createChatRoom(User sender, User recipient) {
        if (sender.getId().equals(recipient.getId())) {
            throw new IllegalArgumentException("Sender ID and Recipient ID must be different");
        }

        String chatId = generateChatId(sender.getId(), recipient.getId());

        ChatRoom chatRoom = ChatRoom.builder()
            .user1(recipient)
            .user2(sender)
            .chatId(chatId)
            .chatState(ChatState.REQUEST_PENDING)
            .requestInitiatorId(sender.getId())
            .build();

        return chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public ChatRoom blockChat(Integer currentUserId, Integer otherUserId) {
        ChatRoom chatRoom = getChatRoom(currentUserId, otherUserId, true)
            .orElseThrow(() -> new IllegalStateException("Chat room could not be created"));
        chatRoom.setChatState(ChatState.BLOCKED);
        chatRoom.setBlockedByUserId(currentUserId);
        return chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public Optional<ChatRoom> unblockChat(Integer currentUserId, Integer otherUserId) {
        Optional<ChatRoom> chatRoomOptional = getChatRoom(currentUserId, otherUserId, false);
        if (chatRoomOptional.isEmpty()) {
            return Optional.empty();
        }

        ChatRoom chatRoom = chatRoomOptional.get();
        if (!ChatState.BLOCKED.equals(chatRoom.getChatState())) {
            return Optional.of(chatRoom);
        }

        if (chatRoom.getBlockedByUserId() != null && !currentUserId.equals(chatRoom.getBlockedByUserId())) {
            throw new IllegalStateException("Only blocker can unblock this chat");
        }

        chatRoom.setChatState(ChatState.ACTIVE);
        chatRoom.setBlockedByUserId(null);
        return Optional.of(chatRoomRepository.save(chatRoom));
    }

    @Transactional
    public Optional<ChatRoom> acceptChatRequest(Integer currentUserId, Integer otherUserId) {
        Optional<ChatRoom> chatRoomOptional = getChatRoom(currentUserId, otherUserId, false);
        if (chatRoomOptional.isEmpty()) {
            return Optional.empty();
        }

        ChatRoom chatRoom = chatRoomOptional.get();
        if (ChatState.BLOCKED.equals(chatRoom.getChatState())) {
            throw new IllegalStateException("Cannot accept a blocked chat");
        }

        if (!ChatState.REQUEST_PENDING.equals(chatRoom.getChatState())) {
            return Optional.of(chatRoom);
        }

        if (currentUserId.equals(chatRoom.getRequestInitiatorId())) {
            throw new IllegalStateException("Request initiator cannot accept own request");
        }

        chatRoom.setChatState(ChatState.ACTIVE);
        chatRoom.setBlockedByUserId(null);
        return Optional.of(chatRoomRepository.save(chatRoom));
    }

    @Transactional
    public void deleteChatForUser(Integer currentUserId, Integer otherUserId) {
        if (currentUserId == null || otherUserId == null) {
            throw new IllegalArgumentException("Current user ID and other user ID must not be null");
        }

        ChatRoom chatRoom = getChatRoom(currentUserId, otherUserId, false)
            .orElseThrow(() -> new IllegalStateException("Chat room not found"));

        Instant now = Instant.now();

        if (chatRoom.getUser1() != null && currentUserId.equals(chatRoom.getUser1().getId())) {
            chatRoom.setUser1DeletedAt(now);
        } else if (chatRoom.getUser2() != null && currentUserId.equals(chatRoom.getUser2().getId())) {
            chatRoom.setUser2DeletedAt(now);
        } else {
            throw new IllegalStateException("Current user is not part of this chat room");
        }

        chatRoomRepository.save(chatRoom);
    }
}
