package com.example.Job_Post.repository;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Job_Post.entity.ChatMessage;
import com.example.Job_Post.entity.ChatRoom;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    
    // Custom query methods can be defined here if needed
    // For example, to find messages by chat room ID or between two users
    List<ChatMessage> findByChatRoomChatId(String chatId);
    
    List<ChatMessage> findBySenderIdAndRecipientId(Integer senderId, Integer recipientId);

    Integer countByRecipientIdAndIsReadFalse(Integer recipientId);

    @Query("""
        select m.recipient.id, count(m)
        from ChatMessage m
        where m.isRead = false
          and m.recipient.id in :recipientIds
        group by m.recipient.id
    """)
    List<Object[]> countUnreadByRecipientIds(@Param("recipientIds") List<Integer> recipientIds);


    @Query("SELECT DISTINCT m.sender.id FROM ChatMessage m " +
        "WHERE m.recipient.id = :currentUserId AND m.isRead = false")
    Set<Integer> findSendersWithUnreadMessages(@Param("currentUserId") Integer currentUserId);

    @Query("""
        SELECT cm FROM ChatMessage cm
        WHERE cm.id = :id
    """)
    ChatMessage getChatMessageByIdLightweight(Integer id);

    @EntityGraph(attributePaths = { "sender", "recipient", "chatRoom" })
    List<ChatMessage> findByChatRoom(ChatRoom chatRoom);

    @EntityGraph(attributePaths = { "sender", "recipient", "chatRoom" })
    List<ChatMessage> findByChatRoomOrderByTimestampDesc(ChatRoom chatRoom, Pageable pageable);

    @EntityGraph(attributePaths = { "sender", "recipient", "chatRoom" })
    List<ChatMessage> findByChatRoomAndTimestampLessThanOrderByTimestampDesc(
        ChatRoom chatRoom,
        Instant before,
        Pageable pageable
    );

    
}
