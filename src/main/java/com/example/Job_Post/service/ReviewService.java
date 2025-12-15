package com.example.Job_Post.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.Job_Post.component.CurrentUser;
import com.example.Job_Post.dto.ReviewDTO;
import com.example.Job_Post.dto.ReviewMapper;
import com.example.Job_Post.entity.JobApplication;
import com.example.Job_Post.entity.Review;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.enumerator.NotificationType;
import com.example.Job_Post.enumerator.SubjectType;
import com.example.Job_Post.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    private final JobApplicationService jobApplicationService;

    private final ReviewMapper reviewMapper;
    private final CurrentUser cUser;

    public ReviewDTO create(
        String review, 
        Integer rating,
        Integer jobApplicationId
    ) {
        JobApplication jobApplication = jobApplicationService.getJobApplicationById(jobApplicationId);

        User writer = cUser.get();
        Boolean isByEmployer = writer.getId().equals(jobApplication.getPost().getCreator().getId());

        if (!isByEmployer && jobApplication.getCreator().getId() != writer.getId()) {
            throw new IllegalArgumentException("User is not authorized to write this review");
        }

        User receiver = isByEmployer ? jobApplication.getCreator() : jobApplication.getPost().getCreator();

        Review reviewObject = Review.builder()
                                    .jobApplication(jobApplication)
                                    .review((review == null || review.strip().length() == 0) ? null : review.strip())
                                    .writer(writer)
                                    .receiver(receiver)
                                    .build();

        Review savedReview = reviewRepository.save(reviewObject);

        if (rating != null && rating > 0 && rating <= 5){
            userService.changeRating("add", receiver, rating, 0);
        }

        if (savedReview != null) {
            try {
                notificationService.sendNotification(receiver, NotificationType.REVIEW, SubjectType.REVIEW, savedReview.getId());
            } catch (Exception e) {
                e.printStackTrace();
                // Handle the exception, maybe log it or rethrow it
                // For now, we will just not send the notification
            }
        }

        return reviewMapper.toDTO(savedReview);
    }

    public ReviewDTO edit (
        Integer reviewId,
        String review, 
        Integer rating
    ) throws IllegalAccessException {
        if (reviewId == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        Review currentReview = reviewRepository.findById(reviewId)
                                    .orElseThrow(() -> new IllegalArgumentException("Unable to find review by id: " + reviewId));

        User currentUser = cUser.get();
        
        if (!currentUser.getId().equals(currentReview.getWriter().getId())){
            throw new IllegalAccessException("Only the creator of this review can edit it");
        }

        Integer ratingBefore = currentReview.getRating();

        currentReview.setRating(rating);
        currentReview.setReview(review);

        Review savedReview = reviewRepository.save(currentReview);

        if (rating != null && rating > 0 && rating <= 5){
            userService.changeRating("change", currentReview.getReceiver(), rating, ratingBefore);
        }

        if (savedReview != null) {
            try {
                notificationService.sendNotification(savedReview.getReceiver(), NotificationType.EDIT_REVIEW, SubjectType.REVIEW, savedReview.getId());
                
            } catch (Exception e) {
                e.printStackTrace();
                // Handle the exception, maybe log it or rethrow it
                // For now, we will just not send the notification
            }
        }

        return reviewMapper.toDTO(savedReview);
    }

    public String deleteReviewById(Integer reviewId) throws IllegalAccessException {
        if (reviewId == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        Review currentReview = reviewRepository.findById(reviewId)
                                    .orElseThrow(() -> new IllegalArgumentException("Unable to find review by id: " + reviewId));
                                
        User currentUser = cUser.get();
        
        if (!currentUser.getId().equals(currentReview.getWriter().getId())){
            throw new IllegalAccessException("Only the creator of this review can edit it");
        }

        reviewRepository.delete(currentReview);

        userService.changeRating("delete", currentReview.getReceiver(), 0, currentReview.getRating());

        return "Review deleted successfully";
 
    }

    public ReviewDTO getReviewById(Integer id){
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        return reviewRepository.findById(id)
                .map(reviewMapper::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Unable to find review by id: " + id));
    }

    public Page<ReviewDTO> getReviewsByReceiver(Integer receiverId, Pageable pageable ){
        if (receiverId == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        User receiver = userService.getUserById(receiverId);

        return reviewRepository.findByReceiver(receiver, pageable)
                .map(reviewMapper::toDTO);
    }

    public Page<ReviewDTO> getReviewsByReceiverAndRating(Integer receiverId, Integer rating, Pageable pageable ){
        if (receiverId == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        User receiver = userService.getUserById(receiverId);

        return reviewRepository.findByReceiverAndRatingIs(receiver, rating, pageable)
                .map(reviewMapper::toDTO);  
    }



    
}
