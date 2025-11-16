package com.example.Job_Post.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.Job_Post.entity.ChatRoom;
import com.example.Job_Post.repository.ChatRoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public ChatRoom getChatRoomByChatId(String chatId) {
        if (chatId == null || chatId.isEmpty()) {
            throw new IllegalArgumentException("Chat ID must not be null or empty");
        }

        return chatRoomRepository.findByChatId(chatId)
            .orElseThrow(() -> new IllegalStateException("Chat room not found for chat ID: " + chatId));
    }

    public Optional<String> getChatId(Integer senderId, Integer recipientId, boolean createNewRoomIfNotExists) {
        if (senderId == null || recipientId == null) {
            throw new IllegalArgumentException("Sender ID and Recipient ID must not be null");
        }

        return chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId)
            .map(ChatRoom::getChatId)
            .or(() -> {
                if (createNewRoomIfNotExists) {
                    var chatId = createChatId(senderId, recipientId);
                    return Optional.of(chatId);
                }
                return Optional.empty();

            });
    }

    private String createChatId(Integer senderId, Integer recipientId) {
        if (senderId == null || recipientId == null) {
            throw new IllegalArgumentException("Sender ID and Recipient ID must not be null");
        }

        if (senderId.equals(recipientId)) {
            throw new IllegalArgumentException("Sender ID and Recipient ID must be different");
        }


        // make chatid starting with bigger one _ small one

        String chatId = (senderId > recipientId) ? senderId + "_" + recipientId : recipientId + "_" + senderId;

        ChatRoom senderRecipient = ChatRoom.builder()
            .senderId(senderId)
            .recipientId(recipientId)
            .chatId(chatId) // Example chat ID generation
            .build();

        ChatRoom recipientSender = ChatRoom.builder()
            .senderId(recipientId)
            .recipientId(senderId)
            .chatId(chatId) // Example chat ID generation
            .build();

        chatRoomRepository.save(senderRecipient);
        chatRoomRepository.save(recipientSender);

        return chatId;
    }
    
}
