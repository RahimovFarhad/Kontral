package com.example.Job_Post.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Job_Post.entity.JobApplication;
import com.example.Job_Post.entity.Post;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.enumerator.JobApplicationStatus;


public interface JobApplicationRepository extends JpaRepository<JobApplication, Integer> {
    @EntityGraph(attributePaths = { "creator", "post", "post.creator", "files" })
    List<JobApplication> findByCreatorAndIsWithdrawnFalse(User creator);

    @EntityGraph(attributePaths = { "creator", "post", "post.creator", "files" })
    List<JobApplication> findByCreatorAndStatusIn(User creator, List<JobApplicationStatus> statuses);

    @EntityGraph(attributePaths = { "creator", "post", "post.creator", "files" })
    Page<JobApplication> findByPostAndIsWithdrawnFalse(Post post, Pageable pageable);

    Integer countByPostIdAndIsWithdrawnFalse(Integer id);

    List<JobApplication> findByPostIdAndIsWithdrawnFalse(Integer postId);
    Integer countByCreatorIdAndIsWithdrawnFalse(Integer creatorId);
    Integer countByCreatorIdAndStatusInAndIsWithdrawnFalse(Integer creatorId, Set<JobApplicationStatus> statuses);
    Integer countByCreatorIdAndStatusAndIsWithdrawnFalse(Integer creatorId, JobApplicationStatus status);
    Integer countByPostCreatorIdAndStatusAndIsWithdrawnFalse(Integer creatorId, JobApplicationStatus status);

    Optional<JobApplication> findByPostIdAndCreatorIdAndIsWithdrawnFalse(Integer postId, Integer creatorId);

    @EntityGraph(attributePaths = { "creator", "post", "post.creator", "files" })
    List<JobApplication> findTop5ByCreatorIdAndStatusInAndIsWithdrawnFalseOrderByAppliedAtDesc(
        Integer creatorId,
        Set<JobApplicationStatus> statuses
    );


    @EntityGraph(attributePaths = { "creator", "post", "post.creator", "files" })
    Page<JobApplication> findByPostCreatorIdAndIsWithdrawnFalse(Integer id, Pageable pageable);

    @EntityGraph(attributePaths = { "creator", "post", "post.creator", "files" })
    List<JobApplication> findTop5ByPostCreatorIdAndStatusAndIsWithdrawnFalseOrderByAppliedAtDesc(
        Integer creatorId,
        JobApplicationStatus status
    );

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

    @Query("""
        select ja.post.id, count(ja)
        from JobApplication ja
        where ja.isWithdrawn = false
          and ja.post.id in :postIds
        group by ja.post.id
    """)
    List<Object[]> countActiveByPostIds(@Param("postIds") List<Integer> postIds);




    
}
