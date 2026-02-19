package com.example.Job_Post.dto;

import com.example.Job_Post.enumerator.JobApplicationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SafeJobApplicationDTO {
    private Integer id; // Unique identifier for the job application
    private UserDTO creatorDTO; // ID of the creator applying for the job
    private PostDTO postDTO; // ID of the job post being applied for

    private JobApplicationStatus status; // Status of the application (e.g., "Pending", "Accepted", "Rejected")

    private String firstName;
    private String lastName;
    private String location;
    private String email;    
}
