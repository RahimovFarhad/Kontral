package com.example.Job_Post.dto;

import org.springframework.stereotype.Component;

import com.example.Job_Post.entity.JobApplication;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class JobApplicationMapper {
    private final FileMapper fileMapper;
    private final UserMapper userMapper;
    private final PostMapper postMapper;
    // This class will contain methods to map between JobApplicationDTO and JobApplication entity
    // For now, it's empty, but you can implement the mapping logic as needed

    // Example method to convert DTO to entity
    public JobApplication toEntity(JobApplicationDTO jobApplicationDTO) {
        if (jobApplicationDTO == null) {
            return null;
        }
        return JobApplication.builder()
                .id(jobApplicationDTO.getId())
                .post(postMapper.toEntity(jobApplicationDTO.getPostDTO()))
                .creator(userMapper.toEntity(jobApplicationDTO.getCreatorDTO()))
                .files(jobApplicationDTO.getFiles().stream().map(fileDTO -> fileMapper.toEntity(fileDTO)).toList())
                .status(jobApplicationDTO.getStatus() != null ? 
                        jobApplicationDTO.getStatus() : null)
                .other(jobApplicationDTO.getOther())
                .firstName(jobApplicationDTO.getFirstName())
                .lastName(jobApplicationDTO.getLastName())
                .email(jobApplicationDTO.getEmail())
                .contactNumber(jobApplicationDTO.getContactNumber())
                .location(jobApplicationDTO.getLocation())
                .appliedAt(jobApplicationDTO.getAppliedAt())
                .finalSalary(jobApplicationDTO.getFinalSalary())
                .build();
    }

    // Example method to convert entity to DTO
    public JobApplicationDTO toDTO(JobApplication jobApplication) {
        if (jobApplication == null) {
            return null;
        }
        return JobApplicationDTO.builder()
                .id(jobApplication.getId())
                .postDTO(postMapper.toDTO(jobApplication.getPost()))
                .creatorDTO(userMapper.toDTO(jobApplication.getCreator()))
                .files(jobApplication.getFiles().stream().map(file -> fileMapper.toDto(file)).toList())
                .status(jobApplication.getStatus() != null ? 
                        jobApplication.getStatus() : null)
                .other(jobApplication.getOther())
                .firstName(jobApplication.getFirstName())
                .lastName(jobApplication.getLastName())
                .email(jobApplication.getEmail())
                .contactNumber(jobApplication.getContactNumber())
                .location(jobApplication.getLocation())
                .appliedAt(jobApplication.getAppliedAt())
                .finalSalary(jobApplication.getFinalSalary())
                .build();
    }
    
}
