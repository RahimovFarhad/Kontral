package com.example.Job_Post.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE )
    private Integer id;

    private String chatRoomId;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = true)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = true)
    private User recipient;

    private String content;

    private LocalDateTime timestamp;

    @Builder.Default
    private Boolean isRead = false; // Indicates if the message has been read by the recipient

    @Builder.Default   
    private Boolean isSystemGenerated = false;

    
}
