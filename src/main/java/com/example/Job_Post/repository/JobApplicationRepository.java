package com.example.Job_Post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("""
        select ja
        from JobApplication ja
        where
            ja.isWithdrawn = false
            and (
                (ja.creator.id = :userAId and ja.post.creator.id = :userBId)
                or
                (ja.creator.id = :userBId and ja.post.creator.id = :userAId)
            )
        order by ja.appliedAt desc, ja.id desc
    """)
    List<JobApplication> findActiveBetweenUsersOrdered(
        @Param("userAId") Integer userAId,
        @Param("userBId") Integer userBId,
        Pageable pageable
    );




    
}
