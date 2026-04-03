package com.example.Job_Post.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.example.Job_Post.enumerator.NegotiationStatus;

import jakarta.persistence.Id;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
    @Index(name = "idx_salary_negotiation_job_app_status", columnList = "job_application_id,status")
})
public class SalaryNegotiation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private JobApplication jobApplication; // link to the offer
    
    @Enumerated(EnumType.STRING)
    private NegotiationStatus status; // ACTIVE, ACCEPTED, REJECTED, CLOSED

    @ManyToOne(fetch = FetchType.LAZY)
    private User initiator; // who initiated the negotiation

    @OneToMany(mappedBy = "negotiation", cascade = CascadeType.ALL, orphanRemoval = true) 
    private List<SalaryOffer> offers = new ArrayList<>();

    @Builder.Default
    private Instant createdAt = Instant.now();
}
