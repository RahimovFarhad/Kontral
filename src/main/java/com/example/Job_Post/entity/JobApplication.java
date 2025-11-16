package com.example.Job_Post.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.Job_Post.enumerator.JobApplicationStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "job_application", indexes = {
    @Index(name = "idx_application_user", columnList = "user_id"),
    @Index(name = "idx_application_post", columnList = "post_id")
    
})
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE )
    private Integer id; // Assuming this is the ID of the job application

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User creator; // ID of the user applying for the job

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post; // ID of the job post being applied for


    @ManyToMany(cascade = {CascadeType.REMOVE})
    @JoinTable(
        name = "job_application_files",
        joinColumns = @JoinColumn(name = "job_application_id"),
        inverseJoinColumns = @JoinColumn(name = "file_id")

    )
    private List<File> files;


    @Enumerated(EnumType.STRING)
    private JobApplicationStatus status; // e.g., "Applied", "Interviewing", "Rejected", "Accepted"

    @Builder.Default
    private LocalDateTime appliedAt = LocalDateTime.now(); // Date when the application was submitted

    private String firstName;
    private String lastName;
    private String contactNumber;
    private String location;
    private String other;
    private String email;

    @Builder.Default
    private boolean isWithdrawn = false;


    @Builder.Default
    @OneToMany(mappedBy = "jobApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "jobApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SalaryNegotiation> negotiations = new ArrayList<>(); // link to the offer

    private Double finalSalary;



    
}
