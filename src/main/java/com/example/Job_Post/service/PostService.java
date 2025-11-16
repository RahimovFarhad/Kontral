package com.example.Job_Post.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.example.Job_Post.dto.PostDTO;
import com.example.Job_Post.dto.PostMapper;
import com.example.Job_Post.entity.Post;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.enumerator.NotificationType;
import com.example.Job_Post.enumerator.SubjectType;
import com.example.Job_Post.repository.JobApplicationRepository;
import com.example.Job_Post.repository.PostRepository;
import com.example.Job_Post.repository.SavedPostRepository;
import com.example.Job_Post.specification.PostSpecification;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final SavedPostRepository savedPostRepository;
    private final JobApplicationRepository jobApplicationRepository;

    private final NotificationService notificationService;

    private final UserService userService;

    public Post create(PostDTO post) {
        System.out.println("Received post data: " + post);
        Post newPost = postMapper.toEntity(post);

        newPost.setCreator(userService.getCurrentUser());
        newPost.setCreatedAt(LocalDateTime.now());

         if (post.getSalary() != null){
            post.setSalaryMin(post.getSalary());
            post.setSalaryMax(post.getSalary());
        }
        
        System.out.println("Creating post: " + newPost);
        return postRepository.save(newPost);
    }

    public Post edit(PostDTO request) {
        User currentUser = userService.getCurrentUser();

        Post post = postRepository.findById(request.getId()).
                        orElseThrow(() -> new EntityNotFoundException("This Post does not exist"));

        if (!post.getCreator().getId().equals(currentUser.getId())) {
            throw new IllegalAccessError("This post does not belong to current user!");
        }

        if (request.getSalary() != null){
            request.setSalaryMin(request.getSalary());
            request.setSalaryMax(request.getSalary());
        }

        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());
        post.setCompanyName(request.getCompanyName());
        post.setLocation(request.getLocation());
        post.setEmploymentType(request.getEmploymentType());
        post.setJobCategory(request.getCategory());
        post.setSalary(request.getSalary());
        post.setSalaryMin(request.getSalaryMin());
        post.setSalaryMax(request.getSalaryMax());
        post.setSalaryCurrency(request.getSalaryCurrency());
        post.setRequirements(request.getRequirements());
        post.setResponsibilities(request.getResponsibilities());
        post.setApplicationDeadline(request.getApplicationDeadline());
        post.setUpdatedAt(LocalDateTime.now());
    

        Post savedPost = postRepository.save(post);

        if (savedPost != null) {
            try {
                // Notify users who have saved this post
                savedPostRepository.findByPostId(savedPost.getId()).forEach(savedPostEntity -> {
                    User user = savedPostEntity.getUser();
                    notificationService.sendNotification(user, NotificationType.EDIT_SAVEDPOST, SubjectType.POST, savedPost.getId());
                });
 
                jobApplicationRepository.findByPostIdAndIsWithdrawnFalse(savedPost.getId()).forEach(jobApplication -> {
                    User user = jobApplication.getCreator();
                    notificationService.sendNotification(user, NotificationType.EDIT_APPLIEDPOST, SubjectType.POST, savedPost.getId());
                });
                
            } catch (Exception e) {
                e.printStackTrace();
                // Handle the exception, maybe log it or rethrow it
                // For now, we will just ignore it
            }


        }

        return savedPost;
    }

    public String deletePostById(Integer id) {
        User currentUser = userService.getCurrentUser();

        Post post = postRepository.findById(id).
                        orElseThrow(() -> new EntityNotFoundException("This Post does not exist"));

        if (!post.getCreator().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("This post does not belong to current user!");
        }

        postRepository.delete(post);



        return "Post deleted successfully";

    } 

    public Page<Post> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Page<Post> getAllPosts(String search, String category, Integer minPrice, 
                                  Integer maxPrice, String employmentType, String sortBy, Pageable pageable) {
        
        // Create combined specification
        var spec = PostSpecification.combineFilters(search, category, minPrice, maxPrice, employmentType);
        
        // Handle custom sorting
        if (sortBy != null && !sortBy.isEmpty()) {
            pageable = applyCustomSort(sortBy, pageable);
        }
        
        return postRepository.findAll(spec, pageable);
    }

    private Pageable applyCustomSort(String sortBy, Pageable pageable) {
        Sort sort = switch (sortBy) {
            case "newest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "oldest" -> Sort.by(Sort.Direction.ASC, "createdAt");
            case "price-high" -> Sort.by(Sort.Direction.DESC, "salary");
            case "price-low" -> Sort.by(Sort.Direction.ASC, "salary");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
        
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    public Post getPostById(Integer id){
        if (id == null) {
            throw new IllegalArgumentException("Post id cannot be null");
        }
        return postRepository.findById(id).
            orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + id));
    }

    public Page<Post> getPostsByCreatorId(Integer userId, Pageable pageable){
        if (userId == null){
            throw new IllegalArgumentException("User id cannot be null");
        }
        return postRepository.findByCreatorId(userId, pageable);
    }

    public Page<Post> getMyPosts(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        return getPostsByCreatorId(currentUser.getId(), pageable);
    }


    
    
}
