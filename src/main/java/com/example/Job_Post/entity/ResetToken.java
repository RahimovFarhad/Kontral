package com.example.Job_Post.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    private String tokenHash;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)    
    private User user;


    private LocalDateTime expiryTime; // Store expiry as a timestamp (milliseconds since epoch)

}
