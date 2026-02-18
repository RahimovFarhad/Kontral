package com.example.Job_Post.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Job_Post.entity.UserRelationshipTag;

public interface UserRelationshipTagRepository extends JpaRepository<UserRelationshipTag, Integer> {

    Optional<UserRelationshipTag> findByUserLowIdAndUserHighId(Integer userLowId, Integer userHighId);

    @Query("""
        select t
        from UserRelationshipTag t
        where
            (t.userLowId = :currentUserId and t.userHighId in :otherUserIds)
            or
            (t.userHighId = :currentUserId and t.userLowId in :otherUserIds)
    """)
    List<UserRelationshipTag> findForCurrentUserAndOtherUsers(
        @Param("currentUserId") Integer currentUserId,
        @Param("otherUserIds") Collection<Integer> otherUserIds
    );
}
