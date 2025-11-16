package com.example.Job_Post.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.Job_Post.entity.Post;


public interface PostRepository extends JpaRepository<Post, Integer>, JpaSpecificationExecutor<Post> {
    // Additional query methods can be defined here if needed
    // For example, to find posts by title or author, you could add:
    // Page<Post> findByTitleContaining(String title);
    // Page<Post> findByAuthorId(Integer authorId);

    Optional<Post> findById(Integer id);

    Page<Post> findByCreatorId(Integer creatorId, Pageable pageable);

    Page<Post> findByTitleContaining(String title, Pageable pageable);

    Page<Post> findByCompanyName(String companyName, Pageable pageable);

    Page<Post> findByEmploymentType(String employmentType, Pageable pageable); // e.g., Full-time, Part-time, Contract

    Page<Post> findByJobCategory(String jobCategory, Pageable pageable); // e.g., "Software Development", "Marketing"

    Page<Post> findByApplicationDeadline(LocalDateTime jobCategory, Pageable pageable); // e.g., "Software Development", "Marketing"
 
    Page<Post> findAll(Pageable pageable);


    





    
}
