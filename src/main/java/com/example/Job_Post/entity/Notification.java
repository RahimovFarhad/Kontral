package com.example.Job_Post.entity;

import java.time.Instant;

import com.example.Job_Post.enumerator.NotificationType;
import com.example.Job_Post.enumerator.SubjectType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
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

@Entity
@Table(indexes = {
    @Index(name = "idx_notification_user_created_id", columnList = "user_id,created_at,id"),
    @Index(name = "idx_notification_user_read_type_created_id", columnList = "user_id,is_read,notification_type,created_at,id")
})
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
    private Instant createdAt = Instant.now(); // Default to current



}
