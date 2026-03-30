package com.example.Job_Post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Job_Post.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {

    // @Query("""
    //     select c from ChatRoom c
    //     where (c.user1.id = :a and c.user2.id = :b)
    //     or (c.user1.id = :b and c.user2.id = :a)
    // """)
    // Optional<ChatRoom> findBetweenUsers(@Param("a") Integer a, @Param("b") Integer b);

    // Additional query methods can be defined here if needed

    Optional<ChatRoom> findByChatId(String chatId);

    @Query("""
        SELECT c FROM ChatRoom c
        WHERE
            (c.user1.id = :userId AND (c.user1DeletedAt IS NULL OR c.lastMessageAt > c.user1DeletedAt))
            OR
            (c.user2.id = :userId AND (c.user2DeletedAt IS NULL OR c.lastMessageAt > c.user2DeletedAt))
        ORDER BY c.lastMessageAt DESC
    """)
    @EntityGraph(attributePaths = { "user1", "user2" })
    List<ChatRoom> findVisibleChatRoomsForUser(@Param("userId") Integer userId);

    

}
