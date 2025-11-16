package com.example.Job_Post.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.example.Job_Post.entity.ResetToken;


public interface ResetTokenRepository extends JpaRepository<ResetToken, Integer> {
    Optional<ResetToken> findByUserEmail(String email);
    Boolean existsByUserEmail(String email);

    @Modifying
    Integer deleteByUserEmail(String email);
}
