package com.example.Job_Post.dto;

import java.time.Instant;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.Job_Post.component.CurrentUser;
import com.example.Job_Post.entity.Post;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.enumerator.PostType;
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
        boolean isCompany = Boolean.TRUE.equals(postDTO.getIsCompany());

        return Post.builder()
                .id(postDTO.getId())
                .creator(userMapper.toEntity(postDTO.getPoster()))
                .title(postDTO.getTitle())
                .description(postDTO.getDescription())
                .companyName(isCompany ? postDTO.getCompanyName() : null)
                .isCompany(isCompany)
                .location(postDTO.getLocation())
                .employmentType(postDTO.getEmploymentType())
                .jobCategory(postDTO.getCategory())
                .postType(resolvePostType(postDTO.getPostType()))
                .salary(postDTO.getSalary())
                .salaryRangeLower(postDTO.getSalaryRangeLower())
                .salaryRangeUpper(postDTO.getSalaryRangeUpper())
                .salaryCurrency(postDTO.getSalaryCurrency())
                .salaryFrequency(postDTO.getSalaryFrequency())
                .serviceDeliveryDays(postDTO.getServiceDeliveryDays())
                .serviceRevisionCount(postDTO.getServiceRevisionCount())
                .serviceIncludes(postDTO.getServiceIncludes())
                .portfolioUrl(postDTO.getPortfolioUrl())
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
        boolean isCompany = Boolean.TRUE.equals(post.getIsCompany());
        PostDTO postDTO = PostDTO.builder()
                .id(post.getId())
                .poster(userMapper.toDTO(post.getCreator()))
                .title(post.getTitle())
                .description(post.getDescription())
                .isCompany(isCompany)
                .companyName(isCompany ? post.getCompanyName() : null)
                .location(post.getLocation())
                .employmentType(post.getEmploymentType())
                .category(post.getJobCategory())
                .postType(resolvePostType(post.getPostType()))
                .salary(post.getSalary())
                .salaryRangeLower(post.getSalaryRangeLower())
                .salaryRangeUpper(post.getSalaryRangeUpper())
                .salaryRange((post.getSalaryRangeLower() != null && post.getSalaryRangeUpper() != null) ? post.getSalaryRangeLower().toString() + '-' + post.getSalaryRangeUpper().toString() : null)
                .salaryCurrency(post.getSalaryCurrency())
                .salaryFrequency(post.getSalaryFrequency())
                .serviceDeliveryDays(post.getServiceDeliveryDays())
                .serviceRevisionCount(post.getServiceRevisionCount())
                .serviceIncludes(post.getServiceIncludes())
                .portfolioUrl(post.getPortfolioUrl())
                .requirements(post.getRequirements())
                .responsibilities(post.getResponsibilities())
                .applicationDeadline(post.getApplicationDeadline())
                .postedTime(post.getCreatedAt())
                .imageUrls(post.getImages() == null ? Collections.emptyList() : post.getImages().stream()
                        .map(postImage -> postImage.getImageUrl())
                        .collect(Collectors.toList()))
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

    public PostDTO toDTOForList(
        Post post,
        UserDTO poster,
        boolean isSavedByCurrentUser,
        int applicationCount,
        List<String> imageUrls
    ) {
        if (post == null) {
            return null;
        }

        return PostDTO.builder()
                .id(post.getId())
                .poster(poster)
                .title(post.getTitle())
                .description(post.getDescription())
                .isCompany(Boolean.TRUE.equals(post.getIsCompany()))
                .companyName(Boolean.TRUE.equals(post.getIsCompany()) ? post.getCompanyName() : null)
                .location(post.getLocation())
                .employmentType(post.getEmploymentType())
                .category(post.getJobCategory())
                .postType(resolvePostType(post.getPostType()))
                .salary(post.getSalary())
                .salaryRangeLower(post.getSalaryRangeLower())
                .salaryRangeUpper(post.getSalaryRangeUpper())
                .salaryRange((post.getSalaryRangeLower() != null && post.getSalaryRangeUpper() != null)
                        ? post.getSalaryRangeLower().toString() + '-' + post.getSalaryRangeUpper().toString()
                        : null)
                .salaryCurrency(post.getSalaryCurrency())
                .salaryFrequency(post.getSalaryFrequency())
                .serviceDeliveryDays(post.getServiceDeliveryDays())
                .serviceRevisionCount(post.getServiceRevisionCount())
                .serviceIncludes(post.getServiceIncludes())
                .portfolioUrl(post.getPortfolioUrl())
                .requirements(post.getRequirements())
                .responsibilities(post.getResponsibilities())
                .applicationDeadline(post.getApplicationDeadline())
                .postedTime(post.getCreatedAt())
                .imageUrls(imageUrls != null ? imageUrls : Collections.emptyList())
                .isSavedByCurrentUser(isSavedByCurrentUser)
                .applicationCount(applicationCount)
                .isNegotiable(post.getIsNegotiable())
                .build();
    }

    public PostDTO toSummaryDTO(Post post) {
        if (post == null) {
            return null;
        }

        User creator = post.getCreator();
        boolean isCompany = Boolean.TRUE.equals(post.getIsCompany());
        return PostDTO.builder()
                .id(post.getId())
                .poster(userMapper.toSummaryDTO(creator))
                .title(post.getTitle())
                .description(post.getDescription())
                .isCompany(isCompany)
                .companyName(isCompany ? post.getCompanyName() : null)
                .location(post.getLocation())
                .employmentType(post.getEmploymentType())
                .category(post.getJobCategory())
                .postType(resolvePostType(post.getPostType()))
                .salary(post.getSalary())
                .salaryRangeLower(post.getSalaryRangeLower())
                .salaryRangeUpper(post.getSalaryRangeUpper())
                .salaryCurrency(post.getSalaryCurrency())
                .salaryFrequency(post.getSalaryFrequency())
                .serviceDeliveryDays(post.getServiceDeliveryDays())
                .serviceRevisionCount(post.getServiceRevisionCount())
                .serviceIncludes(post.getServiceIncludes())
                .portfolioUrl(post.getPortfolioUrl())
                .applicationDeadline(post.getApplicationDeadline())
                .postedTime(post.getCreatedAt())
                .isNegotiable(post.getIsNegotiable())
                .build();
    }

    private PostType resolvePostType(PostType postType) {
        return postType == null ? PostType.JOB_REQUEST : postType;
    }
}
