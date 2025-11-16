package com.example.Job_Post.entity;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post", indexes = {
    @Index(name = "idx_post_user", columnList = "user_id"),
    @Index(name = "idx_post_title", columnList = "title"),
    @Index(name = "idx_post_category", columnList = "jobCategory"),
    @Index(name = "idx_post_salary", columnList = "salary"),
    @Index(name = "idx_post_employment_type", columnList = "employmentType"),
    @Index(name = "idx_post_created_at", columnList = "createdAt")
})
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE )
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User creator; // Assuming this is the ID of the user who created the post

    private String title;
    
    @Column(columnDefinition = "TEXT") 
    private String description;

    private String location;
    private String companyName; 

    private String employmentType; // e.g., Full-time, Part-time, Contract
    private String jobCategory; // e.g., "Software Development", "Marketing"

    private Double salary; // e.g., "50,00"
    private Double salaryMin;
    private Double salaryMax;
    private String salaryCurrency; // e.g., "USD", "EUR"
    private String salaryFrequency; // e.g., "per year", "per hour", "total"
    private Boolean isSalaryNegotiable; // e.g., true if salary is negotiable

    @Column(columnDefinition = "TEXT")
    private String requirements; // e.g., "Bachelor's degree in Computer Science or related field"
    @Column(columnDefinition = "TEXT")
    private String responsibilities; // e.g., "Develop and maintain web applications"
    
    private LocalDateTime applicationDeadline; // e.g., "2023-12-31"

    private LocalDateTime createdAt; 
    private LocalDateTime updatedAt;


    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobApplication> applications;

    private Boolean isNegotiable;

}
