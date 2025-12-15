package com.example.Job_Post.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Job_Post.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    
    // Custom query methods can be defined here if needed
    // For example, to find messages by chat room ID or between two users
    List<ChatMessage> findByChatRoomId(String chatId);
    
    List<ChatMessage> findBySenderIdAndRecipientId(Integer senderId, Integer recipientId);

    Integer countByRecipientIdAndIsReadFalse(Integer recipientId);


    @Query("SELECT DISTINCT m.sender.id FROM ChatMessage m " +
        "WHERE m.recipient.id = :currentUserId AND m.isRead = false")
    Set<Integer> findSendersWithUnreadMessages(@Param("currentUserId") Integer currentUserId);

    @Query("""
        SELECT cm FROM ChatMessage cm
        WHERE cm.id = :id
    """)
    ChatMessage getChatMessageByIdLightweight(Integer id);


    
}
