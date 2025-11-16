package com.example.Job_Post.dto;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.example.Job_Post.entity.SalaryNegotiation;
import com.example.Job_Post.enumerator.NegotiationStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SalaryNegotiationMapper {
    private final JobApplicationMapper jobApplicationMapper;
    private final UserMapper userMapper;
    private final @Lazy SalaryOfferMapper salaryOfferMapper; 

    public SalaryNegotiation toEntity(SalaryNegotiationDTO dto) {
        if (dto == null) {
            return null;
        }

        return SalaryNegotiation.builder()
                .id(dto.getId())
                .jobApplication(jobApplicationMapper.toEntity(dto.getJobApplication()))
                .status(dto.getStatus() != null ? NegotiationStatus.fromString(dto.getStatus()) : null)
                .offers(dto.getOffers() != null ? dto.getOffers().stream()
                        .map(salaryOfferMapper::toEntity)
                        .toList() : null)
                .createdAt(dto.getCreatedAt())
                .initiator(userMapper.toEntity(dto.getInitiator()))
                .build();
    }

    public SalaryNegotiationDTO toDTO(SalaryNegotiation entity) {
        if (entity == null) {
            return null;
        }

        return SalaryNegotiationDTO.builder()
                .id(entity.getId())
                .jobApplication(jobApplicationMapper.toDTO(entity.getJobApplication()))
                .status(entity.getStatus().toString())
                .offers(entity.getOffers() != null ? entity.getOffers().stream()
                        .map(salaryOfferMapper::toDTO)
                        .toList() : null)
                .createdAt(entity.getCreatedAt())
                .initiator(userMapper.toDTO(entity.getInitiator()))
                .build();
    }
    
}
