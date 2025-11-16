package com.example.Job_Post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Job_Post.entity.Review;
import com.example.Job_Post.entity.User;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    Page<Review> findByReceiver(User receiver, Pageable pageable);
    Page<Review> findByWriter(User writer, Pageable pageable);


    Page<Review> findByReceiverAndRatingIs(User receiver, Integer rating, Pageable pageable);
    


}
