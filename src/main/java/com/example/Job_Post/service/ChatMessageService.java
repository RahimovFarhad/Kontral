package com.example.Job_Post.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Job_Post.entity.ChatMessage;
import com.example.Job_Post.repository.ChatMessageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;

    public ChatMessage saveMessage(ChatMessage chatMessage) {
        if (chatMessage.getSender() == null || chatMessage.getRecipient() == null || chatMessage.getContent() == null || chatMessage.getContent().isEmpty()) {
            throw new IllegalArgumentException("Sender, Recipient, and content must not be null or empty");
        }

        String chatId = chatRoomService.getChatId(chatMessage.getSender().getId(), chatMessage.getRecipient().getId(), true)
            .orElseThrow(() -> new IllegalStateException("Chat room could not be created"));

        chatMessage.setChatRoomId(chatId);
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessage.setIsRead(false); // Default to unread

        return chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessage> getChatMessages (Integer senderID, Integer recipientID){
        if (senderID == null || recipientID == null) {
            throw new IllegalArgumentException("Sender ID and Recipient ID must not be null");
        }

        String chatId = chatRoomService.getChatId(senderID, recipientID, false)
            .orElseThrow(() -> new IllegalStateException("Chat room not found"));

        if (chatId == null || chatId==""){
            throw new IllegalArgumentException("Chat room not found");
        }
        return chatMessageRepository.findByChatRoomId(chatId);
    }

    public ChatMessage getChatMessageById(Integer id) {
        return chatMessageRepository.findById(id)
            .orElseThrow(() -> new IllegalStateException("Chat Message not found"));
    }

    public ChatMessage setMessageIsRead(Integer id) {
        ChatMessage chatMessage = chatMessageRepository.findById(id)
            .orElseThrow(() -> new IllegalStateException("Chat Message not found"));

        chatMessage.setIsRead(true);
        return chatMessageRepository.save(chatMessage);
    }

}
