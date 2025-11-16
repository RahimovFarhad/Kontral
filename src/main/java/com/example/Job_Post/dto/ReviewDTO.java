package com.example.Job_Post.dto;

import java.time.LocalDateTime;

import com.example.Job_Post.entity.JobApplication;
import com.example.Job_Post.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Integer id;
    private JobApplicationDTO jobApplication;
    private UserDTO writer;
    private UserDTO receiver;
    private String review;
    private Integer rating;
    private LocalDateTime createdAt;
}
