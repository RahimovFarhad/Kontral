package com.example.Job_Post.entity;


import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_review_job_application_writer", columnNames = {"job_application_id", "writer_id"})
    }
)
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_application_id", foreignKey = @ForeignKey(name = "fk_review_job_application"))
    private JobApplication jobApplication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", foreignKey = @ForeignKey(name = "fk_review_writer"))
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", foreignKey = @ForeignKey(name = "fk_review_receiver"))
    private User receiver;
    
    private String review;

    private Integer rating;

    @Builder.Default
    private final Instant createdAt = Instant.now();

}
