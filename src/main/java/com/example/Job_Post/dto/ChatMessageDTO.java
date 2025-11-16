package com.example.Job_Post.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ChatMessageDTO {

    private Integer id;
    private String tempId;
    private Integer recipientId;
    private Integer senderId; // Optional, if needed for the response
    private String content;
    private String chatRoomId; // Unique for every user pair, e.g., user1_user2 
    private Boolean isRead;
    private LocalDateTime timestamp;

    //newly added (not added to entity yet):
    private Boolean isSystemGenerated;


}
