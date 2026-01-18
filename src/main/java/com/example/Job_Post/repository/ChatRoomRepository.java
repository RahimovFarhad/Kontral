package com.example.Job_Post.repository;

import java.util.List;
import java.util.Optional;

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

    List<ChatRoom> findByUser1_IdOrUser2_IdOrderByLastMessageAtDesc(Integer userId1, Integer userId2);

    

}
