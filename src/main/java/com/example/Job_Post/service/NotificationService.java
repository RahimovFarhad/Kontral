package com.example.Job_Post.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.Job_Post.dto.NotificationDTO;
import com.example.Job_Post.dto.NotificationMapper;
import com.example.Job_Post.entity.ChatNotification;
import com.example.Job_Post.entity.Notification;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.enumerator.NotificationType;
import com.example.Job_Post.enumerator.SubjectType;
import com.example.Job_Post.repository.NotificationRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final NotificationContentGenerator notificationContentGenerator;

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationMapper notificationMapper;


    public Notification sendNotification(User receiver, NotificationType type, SubjectType subjectType, Integer subjectId) {
        Notification notification = Notification.builder()
            .notifiedUser(receiver)
            .notificationType(type)
            .subjectType(subjectType)
            .subjectId(subjectId)
            .build();

        String content = "";
        try {
            content = notificationContentGenerator.generateContent(notification);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception, maybe log it or rethrow it
            // For now, we will just set content to an empty string
        }

        notification.setContent(content != null ? content : "You have a new " + type.toString() + " notification.");
        // System.out.println(receiver.getId());
        
        notification = notificationRepository.save(notification);
        NotificationDTO notificationDTO = notificationMapper.toDTO(notification);

        try {
            String destination = "/topic/notifications/" + receiver.getEmail(); // each user has own channel
            messagingTemplate.convertAndSend(destination, notificationDTO);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception, maybe log it or rethrow it
            // For now, we will just set content to an empty string
        }

        

        return notification;
    }


    public Notification getNotificationById(Integer id) throws IllegalAccessException {
        // Logic to retrieve a notification by its ID
        Notification notification = notificationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Notification cannot be found with id: " + id));

        User currentUser = userService.getCurrentUser();

        if (!notification.getNotifiedUser().equals(currentUser)){
            throw new IllegalAccessException("Notification does not belong to the current user");
        }

        return notification; 
    }

    public Page<Notification> getAllMyNotifications(Pageable pageable) {
        // Logic to retrieve all notifications for the current user with pagination
        User currentUser = userService.getCurrentUser();
        
        return notificationRepository.findByNotifiedUserId(currentUser.getId(), pageable); 
    }

    public Page<Notification> getNotificationsByUserId(Integer userId, Pageable pageable) {
        // Logic to retrieve notifications for a specific user with pagination
        // return notificationRepository.findByNotifiedUserId(userId, pageable); 

        return null; // Placeholder return statement
        //This method should only be for a potential admin user to get all notifications of a user
        
    }

    public String save(Notification notification){
        if (notification == null || notification.getId() == null){
            throw new IllegalArgumentException("Notification cannot be null");
        }
        notificationRepository.save(notification);
        return "Notification saved successfully";
    }

    @Transactional
    public int setAllNotificationsRead(User user){
        return notificationRepository.markAllAsReadByUserId(user.getId());
    }
    
}
