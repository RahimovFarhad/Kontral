package com.example.Job_Post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Job_Post.entity.Post;
import com.example.Job_Post.entity.SavedPost;
import com.example.Job_Post.entity.User;

public interface SavedPostRepository extends JpaRepository<SavedPost, Integer>{


    Optional<SavedPost> findById(Integer id);

    @EntityGraph(attributePaths = { "post", "post.creator", "user" })
    Page<SavedPost> findByUser(User user, Pageable pageable);


    Page<SavedPost> findByPost(Post post, Pageable pageable);

    List<SavedPost> findByPostId(Integer postId);

    Optional<SavedPost> findByPostIdAndUserId(Integer postId, Integer userId);

    boolean existsByPostIdAndUserId(Integer postId, Integer userId);

    @Query("""
        select sp.post.id
        from SavedPost sp
        where sp.user.id = :userId
          and sp.post.id in :postIds
    """)
    List<Integer> findSavedPostIdsByUserIdAndPostIds(
        @Param("userId") Integer userId,
        @Param("postIds") List<Integer> postIds
    );

    


    
}
