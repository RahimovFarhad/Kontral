package com.example.Job_Post.dto;

import org.springframework.stereotype.Component;

import com.example.Job_Post.entity.Review;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReviewMapper {
    private final UserMapper userMapper;
    private final JobApplicationMapper jobApplicationMapper;
    
    public Review toEntity(ReviewDTO reviewDTO) {
        if (reviewDTO == null) {
            return null;
        }

        return Review.builder()
                .id(reviewDTO.getId())
                .jobApplication(jobApplicationMapper.toEntity(reviewDTO.getJobApplication()))
                .writer(userMapper.toEntity(reviewDTO.getWriter()))
                .receiver(userMapper.toEntity(reviewDTO.getReceiver()))
                .review(reviewDTO.getReview())
                .rating(reviewDTO.getRating())
                .createdAt(reviewDTO.getCreatedAt())
                .build();
    }

    public ReviewDTO toDTO(Review review) {
        if (review == null) {
            return null;
        }

        return ReviewDTO.builder()
                .id(review.getId())
                .jobApplication(jobApplicationMapper.toDTO(review.getJobApplication()))
                .writer(userMapper.toDTO(review.getWriter()))
                .receiver(userMapper.toDTO(review.getReceiver()))
                .review(review.getReview())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .build();
    }

}
