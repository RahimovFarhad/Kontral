package com.example.Job_Post.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SalaryOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private SalaryNegotiation negotiation;

    @ManyToOne(fetch = FetchType.LAZY)
    private User sender; // applicant or company

    private Double proposedSalary;
    private String message; // optional message
    private boolean accepted; // whether the receiver accepted this specific offer
    private boolean isResponded; // whether the offer has been responded to

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
