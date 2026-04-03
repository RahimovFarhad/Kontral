package com.example.Job_Post.entity;

import java.time.Instant;

import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(indexes = {
    @Index(name = "idx_salary_offer_negotiation_id_desc", columnList = "negotiation_id,id")
})
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
    private Instant createdAt = Instant.now();
}
