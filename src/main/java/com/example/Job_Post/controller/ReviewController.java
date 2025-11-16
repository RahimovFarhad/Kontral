package com.example.Job_Post.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.Job_Post.dto.PagedResponse;
import com.example.Job_Post.dto.ReviewDTO;
import com.example.Job_Post.entity.Review;
import com.example.Job_Post.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/review")
public class ReviewController {
    private ReviewService reviewService;

    @PostMapping("/create")
    public ResponseEntity<?> writeReview(
        @RequestParam Integer jobApplicationId,
        @RequestParam String review,
        @RequestParam Integer rating
    ) {
        try {
            return ResponseEntity.ok(reviewService.create(review, rating, jobApplicationId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("unable to create review: " + e.getMessage());
        }
        
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editReview(
        @RequestParam Integer reviewId,
        @RequestParam String review,
        @RequestParam Integer rating
    ) {
        try {
            return ResponseEntity.ok(reviewService.edit(reviewId, review, rating));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("unable to edit review: " + e.getMessage());
        }
        
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteReview(
        @RequestParam Integer reviewId
    ) {
        try {
            return ResponseEntity.ok(reviewService.deleteReviewById(reviewId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Unable to delete review: " + e.getMessage());
        }
        
    }


    @GetMapping("/all")
    @ResponseBody
    public ResponseEntity<?> getAllReviewsByReceiverId(
        @RequestParam Integer receiverId,
        @PageableDefault(size = 10, sort = "createdAt") Pageable pageable   
    ){
        try {
            Page<ReviewDTO> page = reviewService.getReviewsByReceiver(receiverId, pageable);
            PagedResponse<ReviewDTO> response = PagedResponse.formPage(page);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Unable to get all reviews: " + e.getMessage());
        }

    }


    @GetMapping("/all-by-rating")
    @ResponseBody
    public ResponseEntity<?> getAllReviewsByReceiverIdAndRating(
        @RequestParam Integer receiverId,
        @RequestParam Integer rating,
        @PageableDefault(size = 10, sort = "createdAt") Pageable pageable   
    ){
        try {
            Page<ReviewDTO> page = reviewService.getReviewsByReceiverAndRating(receiverId, rating, pageable);
            PagedResponse<ReviewDTO> response = PagedResponse.formPage(page);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Unable to get all reviews by this rating: " + e.getMessage());
        }

    }



}
