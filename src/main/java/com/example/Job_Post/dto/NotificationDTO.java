package com.example.Job_Post.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Integer id; // Unique identifier for the job application
    private UserDTO notifiedUserDTO; // ID of the creator applying for the job
    private String notificationType; // ID of the job post being applied for

    private Integer subjectId; // Status of the application (e.g., "Pending", "Accepted", "Rejected")
    private String subjectType;

    private String content;
    private boolean isRead;
    private LocalDateTime createdAt;

    
}
