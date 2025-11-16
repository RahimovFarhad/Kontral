package com.example.Job_Post.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Job_Post.dto.NotificationDTO;
import com.example.Job_Post.dto.NotificationMapper;
import com.example.Job_Post.dto.PagedResponse;
import com.example.Job_Post.entity.Notification;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.service.NotificationService;
import com.example.Job_Post.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;
    private final UserService userService;

    @GetMapping("/mine/all")
    public ResponseEntity<?> getAllMyNotifications(
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ){
        try {
            Page<NotificationDTO> page = notificationService.getAllMyNotifications(pageable).map(notificationMapper::toDTO);
            PagedResponse<NotificationDTO> res = PagedResponse.formPage(page);

            return ResponseEntity.ok(res);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Unable to fetch all notifications: " + e.getMessage() );
        }
    }

    @PutMapping("/read/{notificationId}")
    public ResponseEntity<?> setNotificationRead(@PathVariable Integer notificationId){
        try {
            Notification notification = notificationService.getNotificationById(notificationId);
            notification.setRead(true);
            notificationService.save(notification);

            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Unable to set notification read: " + e.getMessage() );
        }
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<?> setAllMyNotificationsRead(Principal principal){
        try {
            User myUser = userService.getUserByEmail(principal.getName());
            int markedNotificationCount = notificationService.setAllNotificationsRead(myUser);
            
            return ResponseEntity.ok(markedNotificationCount + " notifications set marked successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Unable to set all notification read: " + e.getMessage() );
        }
    }

    
}
