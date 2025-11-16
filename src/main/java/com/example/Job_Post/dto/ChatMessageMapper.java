package com.example.Job_Post.dto;

import org.springframework.stereotype.Component;

import com.example.Job_Post.entity.ChatMessage;
import com.example.Job_Post.service.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatMessageMapper {
    private final UserService userService;
    
    public ChatMessageDTO toDTO(ChatMessage chatMessage) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setRecipientId(chatMessage.getRecipient().getId());
        dto.setContent(chatMessage.getContent());
        dto.setChatRoomId(chatMessage.getChatRoomId());
        dto.setSenderId(chatMessage.getSender().getId()); 
        dto.setIsRead(chatMessage.getIsRead());
        dto.setId(chatMessage.getId());
        dto.setTimestamp(chatMessage.getTimestamp());
        dto.setIsSystemGenerated(chatMessage.getIsSystemGenerated());
        return dto;
    }

    public ChatMessage toEntity(ChatMessageDTO dto) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(dto.getContent());
        // Assuming you have methods to fetch User entities by ID
        chatMessage.setRecipient(userService.getUserById(dto.getRecipientId()));
        chatMessage.setIsRead(dto.getIsRead());
        // Set chatRoom based on chatRoomId if necessary
        return chatMessage;
    }

    
}
