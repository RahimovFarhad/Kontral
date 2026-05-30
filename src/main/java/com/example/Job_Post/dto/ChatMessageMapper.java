package com.example.Job_Post.dto;

import org.springframework.stereotype.Component;

import com.example.Job_Post.entity.ChatMessage;

import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
public class ChatMessageMapper {

    public ChatMessageDTO toDTO(ChatMessage chatMessage) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setRecipientId(chatMessage.getRecipient().getId());
        dto.setContent(chatMessage.getContent());
        dto.setChatRoomId(chatMessage.getChatRoom().getChatId());
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
        chatMessage.setIsRead(dto.getIsRead());
        // Caller is responsible for setting sender and recipient
        return chatMessage;
    }

    
}
