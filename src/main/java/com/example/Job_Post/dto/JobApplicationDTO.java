package com.example.Job_Post.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.Job_Post.enumerator.JobApplicationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationDTO {
    private Integer id; // Unique identifier for the job application
    private UserDTO creatorDTO; // ID of the creator applying for the job
    private PostDTO postDTO; // ID of the job post being applied for

    private List<FileDTO> files;
    private JobApplicationStatus status; // Status of the application (e.g., "Pending", "Accepted", "Rejected")

    private String firstName;
    private String lastName;
    private String contactNumber;
    private String location;
    private String other;
    private String email;

    private LocalDateTime appliedAt;
    private Double finalSalary;
    
}
