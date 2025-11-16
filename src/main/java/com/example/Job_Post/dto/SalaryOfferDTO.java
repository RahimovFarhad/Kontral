package com.example.Job_Post.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SalaryOfferDTO {
    private Integer id;
    private Integer negotiationId;
    private UserDTO sender;
    private Double proposedSalary;
    private String message;
    private boolean accepted;
    private boolean isResponded;
    private LocalDateTime createdAt;
}
