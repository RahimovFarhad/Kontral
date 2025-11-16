package com.example.Job_Post.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.Job_Post.entity.User;
import com.example.Job_Post.enumerator.Status;

public interface UserRepository extends JpaRepository<User, Integer> {
    
    Optional<User> findByEmail(String email); 
    Boolean existsByEmail(String email);
    Boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT u FROM User u WHERE u.verified = true")
    List<User> findAll();

    @Query("SELECT u FROM User u WHERE u.verified = true")
    List<User> findAllByStatus(Status status); 
    

}
