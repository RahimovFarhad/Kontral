package com.example.Job_Post.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "files")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor

public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE )
    private Integer id;

    private String fileName;
    private String fileUrl;

    private LocalDateTime uploadedAt;
    private Double size;

    private Boolean isActive;
    
    @jakarta.persistence.ManyToOne
    @jakarta.persistence.JoinColumn(name = "user_id", nullable = true)
    private User user;

}
