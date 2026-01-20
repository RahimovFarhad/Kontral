package com.example.Job_Post.entity;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatNotification { //this is not an entity. You need to change it. 
    private Integer id;
    private String tempId; //optional
    private Integer senderId;
    private Integer recipientId;
    private String content;
    private Instant timestamp;
    private Boolean isSystemGenerated = false;



}
