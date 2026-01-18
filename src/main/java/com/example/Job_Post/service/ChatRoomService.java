package com.example.Job_Post.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.Job_Post.entity.ChatRoom;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.repository.ChatRoomRepository;

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
            .build();

        return chatRoomRepository.save(senderRecipient);
    }

    
    
}
