package com.example.Job_Post.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Job_Post.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {

    Optional<ChatRoom> findBySenderIdAndRecipientId(Integer senderId, Integer recipientId);
    // Additional query methods can be defined here if needed

    Optional<ChatRoom> findByChatId(String chatId);

    

}
