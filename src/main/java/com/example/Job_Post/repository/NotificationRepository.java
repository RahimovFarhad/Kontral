package com.example.Job_Post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Job_Post.entity.Notification;
import com.example.Job_Post.enumerator.NotificationType;


public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    
    // Custom query methods can be added here if needed
    // For example, to find notifications by user or type

    @EntityGraph(attributePaths = { "notifiedUser" })
    Page<Notification> findByNotifiedUserId(Integer userId, Pageable pageable); 
    Integer countByNotifiedUserIdAndIsReadFalse(Integer userId);    
    Optional<Notification> findTopByNotifiedUserIdAndIsReadFalseAndNotificationTypeInOrderByCreatedAtDescIdDesc(
        Integer userId,
        List<NotificationType> notificationTypes
    );

    @Query("""
        select n.notifiedUser.id, count(n)
        from Notification n
        where n.isRead = false
          and n.notifiedUser.id in :userIds
        group by n.notifiedUser.id
    """)
    List<Object[]> countUnreadByUserIds(@Param("userIds") List<Integer> userIds);


    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.notifiedUser.id = :userId")
    int markAllAsReadByUserId(@Param("userId") Integer userId);

    @Modifying
    @Query("""
        UPDATE Notification n
        SET n.isRead = true
        WHERE n.notifiedUser.id = :userId
        AND n.id IN :notificationIds
        AND n.isRead = false
    """)
    int markAsReadByUserIdAndIds(
        @Param("userId") Integer userId,
        @Param("notificationIds") List<Integer> notificationIds
    );


    
}
