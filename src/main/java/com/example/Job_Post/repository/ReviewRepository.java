package com.example.Job_Post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Job_Post.entity.Review;
import com.example.Job_Post.entity.User;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    @EntityGraph(attributePaths = {
        "writer", "receiver", "jobApplication", "jobApplication.creator", "jobApplication.post", "jobApplication.post.creator"
    })
    Page<Review> findByReceiver(User receiver, Pageable pageable);
    Page<Review> findByWriter(User writer, Pageable pageable);


    @EntityGraph(attributePaths = {
        "writer", "receiver", "jobApplication", "jobApplication.creator", "jobApplication.post", "jobApplication.post.creator"
    })
    Page<Review> findByReceiverAndRatingIs(User receiver, Integer rating, Pageable pageable);
    


}
