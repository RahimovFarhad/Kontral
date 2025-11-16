package com.example.Job_Post.dto;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.example.Job_Post.entity.Post;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.repository.SavedPostRepository;
import com.example.Job_Post.service.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostMapper {
    private final UserMapper userMapper;
    private final SavedPostRepository savedPostRepository;
    private final UserService userService;
    
    public Post toEntity(PostDTO postDTO) {
        if (postDTO == null) {
            return null;
        }

        return Post.builder()
                .id(postDTO.getId())
                .creator(userMapper.toEntity(postDTO.getPoster()))
                .title(postDTO.getTitle())
                .description(postDTO.getDescription())
                .companyName(postDTO.getCompanyName())
                .location(postDTO.getLocation())
                .employmentType(postDTO.getEmploymentType())
                .jobCategory(postDTO.getCategory())
                .salary(postDTO.getSalary())
                .salaryMax(postDTO.getSalaryMax())
                .salaryMax(postDTO.getSalaryMin())
                .salaryCurrency(postDTO.getSalaryCurrency())
                .salaryFrequency(postDTO.getSalaryFrequency())
                .isSalaryNegotiable(postDTO.getIsSalaryNegotiable())
                .requirements(postDTO.getRequirements())
                .responsibilities(postDTO.getResponsibilities())
                .applicationDeadline(postDTO.getApplicationDeadline())
                .updatedAt(LocalDateTime.now()) // Assuming this is the last updated time
                .createdAt(postDTO.getPostedTime())
                .isNegotiable(postDTO.getIsNegotiable())
                .build();
    }

    public PostDTO toDTO(Post post) {
        if (post == null) {
            return null;
        }        

        PostDTO postDTO = PostDTO.builder()
                .id(post.getId())
                .poster(userMapper.toDTO(post.getCreator()))
                .title(post.getTitle())
                .description(post.getDescription())
                .companyName(post.getCompanyName())
                .location(post.getLocation())
                .employmentType(post.getEmploymentType())
                .category(post.getJobCategory())
                .salary(post.getSalary())
                .salaryMin(post.getSalaryMin())
                .salaryMax(post.getSalaryMax())
                .salaryRange((post.getSalaryMin() != null && post.getSalaryMax() != null) ? post.getSalaryMin().toString() + '-' + post.getSalaryMax().toString() : null)
                .salaryCurrency(post.getSalaryCurrency())
                .salaryFrequency(post.getSalaryFrequency())
                .isSalaryNegotiable(post.getIsSalaryNegotiable())
                .requirements(post.getRequirements())
                .responsibilities(post.getResponsibilities())
                .applicationDeadline(post.getApplicationDeadline())
                .postedTime(post.getCreatedAt())
                .isSavedByCurrentUser(false)
                .applicationCount(post.getApplications() != null ? post.getApplications().size() : 0)
                .isNegotiable(post.getIsNegotiable())
                .build();

        try {
            User currentUser = userService.getCurrentUser();
            postDTO.setIsSavedByCurrentUser(savedPostRepository.existsByPostIdAndUserId(post.getId(), currentUser.getId()));
        } catch (Exception e) {
        }
  


        return postDTO;
    }

    
}
