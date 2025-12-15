package com.example.Job_Post.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Job_Post.dto.ChatUserDTO;
import com.example.Job_Post.dto.UserWebSocketDTO;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.enumerator.Status;

public interface UserRepository extends JpaRepository<User, Integer> {


    @Query("""
        SELECT new com.example.Job_Post.dto.UserWebSocketDTO(
        u.id, u.email, u.nickName, u.status
        )
        FROM User u
        WHERE u.email = :email
    """)
    UserWebSocketDTO findWebSocketUserByEmail(@Param("email") String email);
    
    @Query("""
        SELECT new com.example.Job_Post.dto.UserWebSocketDTO(
        u.id, u.email, u.nickName, u.status
        )
        FROM User u
        WHERE u.id = :id
    """)
    UserWebSocketDTO findWebSocketUserById(@Param("id") Integer id);
    
    Optional<User> findByEmail(String email); 

    Boolean existsByEmail(String email);
    Boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT u FROM User u WHERE u.verified = true")
    List<User> findAll();

    @Query("SELECT u FROM User u WHERE u.verified = true")
    List<User> findAllByStatus(Status status); 

    Optional<User> findById(Integer id);


    @Query("""
    select new com.example.Job_Post.dto.ChatUserDTO(
        u.id,
        u.nickName,
        u.imageUrl,
        u.status,
        u.email
    )
    from User u
    """)
    List<ChatUserDTO> findAllChatUsersLight();



    

}
