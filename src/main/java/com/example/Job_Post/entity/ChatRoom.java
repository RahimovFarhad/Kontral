package com.example.Job_Post.entity;

import java.time.Instant;

import com.example.Job_Post.enumerator.ChatState;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(indexes = {
    @Index(name = "idx_chat_room_chat_id", columnList = "chat_id"),
    @Index(name = "idx_chat_room_user1_last_message", columnList = "user1_id,last_message_at"),
    @Index(name = "idx_chat_room_user2_last_message", columnList = "user2_id,last_message_at")
})
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    private String chatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", nullable = true)
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", nullable = true)
    private User user2;


    private Instant createdAt;

    @Builder.Default
    private Instant lastMessageAt = Instant.now();

    private Instant user1DeletedAt;

    private Instant user2DeletedAt;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ChatState chatState = ChatState.REQUEST_PENDING;

    private Integer requestInitiatorId;

    private Integer blockedByUserId;
}
