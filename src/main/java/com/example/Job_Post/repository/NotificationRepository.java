package com.example.Job_Post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Job_Post.entity.Notification;


public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    
    // Custom query methods can be added here if needed
    // For example, to find notifications by user or type

    Page<Notification> findByNotifiedUserId(Integer userId, Pageable pageable); 
    Integer countByNotifiedUserIdAndIsReadFalse(Integer userId);    


    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.notifiedUser.id = :userId")
    int markAllAsReadByUserId(@Param("userId") Integer userId);

    
}
