package com.example.Job_Post.entity;

import java.time.LocalDateTime;

import com.example.Job_Post.enumerator.NotificationType;
import com.example.Job_Post.enumerator.SubjectType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User notifiedUser;

    @Enumerated
    private NotificationType notificationType;

    private Integer subjectId;
    private SubjectType subjectType; // "post", "user", "review", "message"

    @Column(columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    private boolean isRead = false; // Default value is false, meaning the notification is unread

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now(); // Default to current



}
