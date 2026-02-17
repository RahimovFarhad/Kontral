package com.example.Job_Post.dto;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.example.Job_Post.component.CurrentUser;
import com.example.Job_Post.entity.Post;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.repository.SavedPostRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostMapper {
    private final UserMapper userMapper;
    private final SavedPostRepository savedPostRepository;
    private final CurrentUser cUser;
    
    public Post toEntity(PostDTO postDTO) {
        if (postDTO == null) {
            return null;
        }

        return Post.builder()
                .id(postDTO.getId())
                .creator(userMapper.toEntity(postDTO.getPoster()))
                .title(postDTO.getTitle())
                .description(postDTO.getDescription())
                .companyName(postDTO.getIsCompany() ? postDTO.getCompanyName() : null)
                .isCompany(postDTO.getIsCompany())
                .location(postDTO.getLocation())
                .employmentType(postDTO.getEmploymentType())
                .jobCategory(postDTO.getCategory())
                .salary(postDTO.getSalary())
                .salaryRangeLower(postDTO.getSalaryRangeLower())
                .salaryRangeUpper(postDTO.getSalaryRangeUpper())
                .salaryCurrency(postDTO.getSalaryCurrency())
                .salaryFrequency(postDTO.getSalaryFrequency())
                .requirements(postDTO.getRequirements())
                .responsibilities(postDTO.getResponsibilities())
                .applicationDeadline(postDTO.getApplicationDeadline())
                .updatedAt(Instant.now()) // Assuming this is the last updated time
                .createdAt(postDTO.getPostedTime())
                .isNegotiable(postDTO.getIsNegotiable())
                .build();
    }

    public PostDTO toDTO(Post post) {
        if (post == null) {
            return null;
        }        

        System.out.println("\nhey\n");
        PostDTO postDTO = PostDTO.builder()
                .id(post.getId())
                .poster(userMapper.toDTO(post.getCreator()))
                .title(post.getTitle())
                .description(post.getDescription())
                .isCompany(post.getIsCompany())
                .companyName(post.getIsCompany() ? post.getCompanyName() : null)
                .location(post.getLocation())
                .employmentType(post.getEmploymentType())
                .category(post.getJobCategory())
                .salary(post.getSalary())
                .salaryRangeLower(post.getSalaryRangeLower())
                .salaryRangeUpper(post.getSalaryRangeUpper())
                .salaryRange((post.getSalaryRangeLower() != null && post.getSalaryRangeUpper() != null) ? post.getSalaryRangeLower().toString() + '-' + post.getSalaryRangeUpper().toString() : null)
                .salaryCurrency(post.getSalaryCurrency())
                .salaryFrequency(post.getSalaryFrequency())
                .requirements(post.getRequirements())
                .responsibilities(post.getResponsibilities())
                .applicationDeadline(post.getApplicationDeadline())
                .postedTime(post.getCreatedAt())
                .isSavedByCurrentUser(false)
                .applicationCount(post.getApplications() != null ? post.getApplications().size() : 0)
                .isNegotiable(post.getIsNegotiable())
                .build();

        try {
            User currentUser = cUser.get();
            postDTO.setIsSavedByCurrentUser(savedPostRepository.existsByPostIdAndUserId(post.getId(), currentUser.getId()));
        } catch (Exception e) {
        }
  


        return postDTO;
    }

    
}
