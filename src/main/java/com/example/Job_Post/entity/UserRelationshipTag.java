package com.example.Job_Post.entity;

import java.time.Instant;

import com.example.Job_Post.enumerator.JobApplicationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "user_relationship_tag",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_relationship_pair", columnNames = {"userLowId", "userHighId"})
    },
    indexes = {
        @Index(name = "idx_user_relationship_low", columnList = "userLowId"),
        @Index(name = "idx_user_relationship_high", columnList = "userHighId")
    }
)
public class UserRelationshipTag {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(nullable = false)
    private Integer userLowId;

    @Column(nullable = false)
    private Integer userHighId;

    @Column(nullable = false)
    private Integer lastApplicationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobApplicationStatus lastApplicationStatus;

    @Column(nullable = false)
    private Integer applicantId;

    @Builder.Default
    @Column(nullable = false)
    private Instant updatedAt = Instant.now();
}
