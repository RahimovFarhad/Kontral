package com.example.Job_Post.dto;

import org.springframework.stereotype.Component;
import com.example.Job_Post.entity.Notification;
import com.example.Job_Post.enumerator.NotificationType;
import com.example.Job_Post.enumerator.SubjectType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationMapper {
    
    private final UserMapper userMapper;
    
    // Method to convert DTO to entity
    public Notification toEntity(NotificationDTO notificationDTO) {
        if (notificationDTO == null) {
            return null;
        }
        
        return Notification.builder()
                .id(notificationDTO.getId())
                .notifiedUser(userMapper.toEntity(notificationDTO.getNotifiedUserDTO()))
                .notificationType(notificationDTO.getNotificationType() != null ? 
                    NotificationType.valueOf(notificationDTO.getNotificationType().toUpperCase()) : null)
                .subjectId(notificationDTO.getSubjectId())
                .subjectType(notificationDTO.getSubjectType() != null ? 
                    SubjectType.valueOf(notificationDTO.getSubjectType().toUpperCase()) : null)
                .content(notificationDTO.getContent())
                .isRead(notificationDTO.isRead())
                .createdAt(notificationDTO.getCreatedAt())
                .build();
    }
    
    // Method to convert entity to DTO
    public NotificationDTO toDTO(Notification notification) {
        if (notification == null) {
            return null;
        }
        
        return NotificationDTO.builder()
                .id(notification.getId())
                .notifiedUserDTO(userMapper.toDTO(notification.getNotifiedUser()))
                .notificationType(notification.getNotificationType() != null ? 
                    notification.getNotificationType().name() : null)
                .subjectId(notification.getSubjectId())
                .subjectType(notification.getSubjectType() != null ? 
                    notification.getSubjectType().name() : null)
                .content(notification.getContent())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}