package com.example.Job_Post.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LatestUnreadActionNotificationDTO {
    private Integer id;
    private String notificationType;
    private Integer chatUserId;
    private boolean read;
    private Instant createdAt;
    private String content;
}
