package com.example.Job_Post.dto;

import java.time.Instant;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SalaryNegotiationDTO {
    private Integer id;
    private JobApplicationDTO jobApplication;
    private String status;
    private List<SalaryOfferDTO> offers;
    private Instant createdAt;
    private UserDTO initiator;
}
