package com.example.Job_Post.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Job_Post.entity.ChatMessage;
import com.example.Job_Post.entity.ChatRoom;
import com.example.Job_Post.repository.ChatMessageRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;

    @Transactional
    public ChatMessage saveMessage(ChatMessage chatMessage) {
        if (chatMessage.getSender() == null || chatMessage.getRecipient() == null || chatMessage.getContent() == null || chatMessage.getContent().isEmpty()) {
            throw new IllegalArgumentException("Sender, Recipient, and content must not be null or empty");
        }

        ChatRoom chatRoom = chatRoomService.getChatRoom(chatMessage.getSender().getId(), chatMessage.getRecipient().getId(), true)
            .orElseThrow(() -> new IllegalStateException("Chat room could not be created"));

        chatMessage.setChatRoom(chatRoom);
        chatMessage.setTimestamp(Instant.now());
        chatMessage.setIsRead(false); // Default to unread


        return chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessage> getChatMessages (Integer senderID, Integer recipientID){
        if (senderID == null || recipientID == null) {
            throw new IllegalArgumentException("Sender ID and Recipient ID must not be null");
        }

        ChatRoom chatRoom = chatRoomService.getChatRoom(senderID, recipientID, false)
            .orElseThrow(() -> new IllegalStateException("Chat room not found"));

        if (chatRoom == null) {
            throw new IllegalArgumentException("Chat room not found");
        }
        return chatMessageRepository.findByChatRoom(chatRoom);
    }

    public ChatMessage getChatMessageById(Integer id) {
        return chatMessageRepository.findById(id)
            .orElseThrow(() -> new IllegalStateException("Chat Message not found"));
    }

    public void setMessageIsRead(Integer messageId) {
        ChatMessage msg = chatMessageRepository.getChatMessageByIdLightweight(messageId);
        msg.setIsRead(true);
        chatMessageRepository.save(msg);
    }

}
