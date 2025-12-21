package com.example.Job_Post.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.Job_Post.dto.ChatMessageDTO;
import com.example.Job_Post.dto.ChatMessageMapper;
import com.example.Job_Post.dto.UserWebSocketDTO;
import com.example.Job_Post.entity.ChatMessage;
import com.example.Job_Post.entity.ChatNotification;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.repository.UserRepository;
import com.example.Job_Post.service.ChatMessageService;
import com.example.Job_Post.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;
    private final ChatMessageMapper chatMessageMapper;

    private final ChatMessageService chatMessageService;
    private final UserRepository userRepository;

    @MessageMapping("/read")
        public void setMessageRead(@Payload ChatMessageDTO chatMessageDTO, 
            Principal principal) throws IllegalAccessException {

        if (principal == null) {
            throw new IllegalStateException("User not authenticated");
        }

        String username = principal.getName();

        UserWebSocketDTO user = userRepository.findWebSocketUserByEmail(username);

        ChatMessage chatMessage = chatMessageService.getChatMessageById(chatMessageDTO.getId());

        if (!chatMessage.getRecipient().getId().equals(user.getId())) {
            throw new IllegalAccessException(
                "Only recipient can read this message"
            );
        }

        chatMessageService.setMessageIsRead(chatMessage.getId());

        messagingTemplate.convertAndSendToUser(
            chatMessage.getSender().getEmail(),
            "/queue/messageStatus",
            ChatNotification.builder()
                .id(chatMessage.getId())
                .recipientId(chatMessage.getRecipient().getId())
                .build()
        );
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessageDTO chatMessageDTO, Principal principal) {

        if (principal == null) {
            throw new IllegalStateException("User not authenticated");
        }

        String username = principal.getName();

        User sender = userService.getUserByEmail(username);

        ChatMessage chatMessage = chatMessageMapper.toEntity(chatMessageDTO);
        chatMessage.setSender(sender);

        if (chatMessage.getSender().getId().equals(chatMessage.getRecipient().getId())) {
            throw new IllegalArgumentException("Cannot send message to yourself");
        }

        ChatMessage savedMessage = chatMessageService.saveMessage(chatMessage);

        // Send to recipient
        messagingTemplate.convertAndSendToUser(
            savedMessage.getRecipient().getEmail(),
            "/queue/messages",
            ChatNotification.builder()
                .id(savedMessage.getId())
                .senderId(savedMessage.getSender().getId())
                .recipientId(savedMessage.getRecipient().getId())
                .content(savedMessage.getContent())
                .timestamp(savedMessage.getTimestamp())
                .build()
        );

        // Send confirmation to sender (replace tempId)
        messagingTemplate.convertAndSendToUser(
            savedMessage.getSender().getEmail(),
            "/queue/sentMessage",
            ChatNotification.builder()
                .tempId(chatMessageDTO.getTempId())
                .id(savedMessage.getId())
                .timestamp(savedMessage.getTimestamp())
                .build()
        );
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<?> getChatMessages(@PathVariable Integer senderId, @PathVariable Integer recipientId, Principal principal) {

        try {
            User sender = userService.getUserById(senderId);
            User recipient = userService.getUserById(recipientId);

            if (!principal.getName().equals(recipient.getEmail()) && !principal.getName().equals(sender.getEmail())){
                throw new IllegalAccessError("This user cannot read these messages");
            }
            List<ChatMessageDTO> messages = chatMessageService.getChatMessages(senderId, recipientId).stream()
                .map(chatMessageMapper::toDTO)
                .toList();
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to retrieve messages: " + e.getMessage());
        }

    }

}
