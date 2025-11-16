package com.example.Job_Post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Job_Post.entity.JobApplication;
import com.example.Job_Post.entity.Post;
import com.example.Job_Post.entity.User;


public interface JobApplicationRepository extends JpaRepository<JobApplication, Integer> {
    List<JobApplication> findByCreatorAndIsWithdrawnFalse(User creator);

    Page<JobApplication> findByPostAndIsWithdrawnFalse(Post post, Pageable pageable);

    Integer countByPostIdAndIsWithdrawnFalse(Integer id);

    List<JobApplication> findByPostIdAndIsWithdrawnFalse(Integer postId);

    Optional<JobApplication> findByPostIdAndCreatorIdAndIsWithdrawnFalse(Integer postId, Integer creatorId);


    Page<JobApplication> findByPostCreatorIdAndIsWithdrawnFalse(Integer id, Pageable pageable);





    
}
