package com.example.Job_Post.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private Integer id;
    private UserDTO poster;

    private String title;
    private String description;

    private String companyName;
    private String location;

    private String employmentType;
    private String category;

    private Double salary; // e.g., "50,000"
    private Double salaryMin;
    private Double salaryMax;
    private String salaryRange;
    private String salaryCurrency; // e.g., "USD", "EUR"
    private String salaryFrequency; // e.g., "per year", "per hour", "total"
    private Boolean isSalaryNegotiable; // e.g., true if salary is negotiable

    private String requirements;
    private String responsibilities;
    
    private LocalDateTime applicationDeadline;
    private LocalDateTime postedTime;

    // Additional fields can be added as needed
    private Boolean isSavedByCurrentUser;

    private Integer applicationCount;
    private Boolean isNegotiable;
}
