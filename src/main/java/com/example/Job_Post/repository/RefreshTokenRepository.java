package com.example.Job_Post.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.example.Job_Post.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByIdAndRevokedFalse(Integer id);

    @Modifying
    Integer deleteByUserEmail(String email);
}
