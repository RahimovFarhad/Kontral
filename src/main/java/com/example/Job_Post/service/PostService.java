package com.example.Job_Post.service;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Job_Post.component.CurrentUser;
import com.example.Job_Post.dto.PostDTO;
import com.example.Job_Post.dto.PostMapper;
import com.example.Job_Post.dto.UserDTO;
import com.example.Job_Post.dto.UserMapper;
import com.example.Job_Post.entity.Post;
import com.example.Job_Post.entity.PostImages;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.enumerator.NotificationType;
import com.example.Job_Post.enumerator.PostType;
import com.example.Job_Post.enumerator.SubjectType;
import com.example.Job_Post.repository.JobApplicationRepository;
import com.example.Job_Post.repository.PostRepository;
import com.example.Job_Post.repository.PostImagesRepository;
import com.example.Job_Post.repository.SavedPostRepository;
import com.example.Job_Post.repository.UserRepository;
import com.example.Job_Post.specification.PostSpecification;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final SavedPostRepository savedPostRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final PostImagesRepository postImagesRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final FileUploadService fileUploadService;

    private final NotificationService notificationService;

    private final CurrentUser cUser;

    public Post create(PostDTO post) {
        return create(post, null, PostType.JOB_REQUEST);
    }

    @Transactional
    public Post create(PostDTO post, List<MultipartFile> images) {
        return create(post, images, PostType.JOB_REQUEST);
    }

    @Transactional
    public Post create(PostDTO post, List<MultipartFile> images, PostType enforcedPostType) {
        System.out.println("Received post data: " + post);
        Post newPost = postMapper.toEntity(post);
 
        newPost.setCreator(cUser.get());
        newPost.setCreatedAt(Instant.now());
        newPost.setPostType(resolvePostType(enforcedPostType));

        if (post.getSalary() == null && (post.getSalaryRangeLower() == null || post.getSalaryRangeUpper() == null)) {
            throw new IllegalArgumentException("Either salary or both salary range bounds must be provided.");
        }

        if (post.getSalary() != null){
            newPost.setSalaryRangeLower(post.getSalary());
            newPost.setSalaryRangeUpper(post.getSalary());
        }

        validateImages(images);
        
        System.out.println("Creating post: " + newPost);
        Post savedPost = postRepository.save(newPost);
        attachImages(savedPost, images);
        return postRepository.save(savedPost);
    }

    public Post edit(PostDTO request) {
        return edit(request, PostType.JOB_REQUEST);
    }

    public Post edit(PostDTO request, PostType expectedPostType) {
        User currentUser = cUser.get();


        Post post = postRepository.findById(request.getId()).
                        orElseThrow(() -> new EntityNotFoundException("This Post does not exist"));
        ensurePostType(post, expectedPostType);

        if (!post.getCreator().getId().equals(currentUser.getId())) {
            throw new IllegalAccessError("This post does not belong to current user!");
        }

        if (request.getSalary() == null && (request.getSalaryRangeLower() == null || request.getSalaryRangeUpper() == null)) {
            throw new IllegalArgumentException("Either salary or both salary range bounds must be provided.");
        }

        if (request.getSalary() != null){
            request.setSalaryRangeLower(request.getSalary());
            request.setSalaryRangeUpper(request.getSalary());
        }

        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());
        boolean isCompany = Boolean.TRUE.equals(request.getIsCompany());
        post.setIsCompany(isCompany);
        post.setCompanyName(isCompany ? request.getCompanyName() : null);
        post.setLocation(request.getLocation());
        post.setEmploymentType(request.getEmploymentType());
        post.setJobCategory(request.getCategory());
        post.setPostType(resolvePostType(expectedPostType));
        post.setSalary(request.getSalary());
        post.setSalaryRangeLower(request.getSalaryRangeLower());
        post.setSalaryRangeUpper(request.getSalaryRangeUpper());
        post.setSalaryCurrency(request.getSalaryCurrency());
        post.setSalaryFrequency(request.getSalaryFrequency());
        post.setIsNegotiable(request.getIsNegotiable());
        post.setServiceDeliveryDays(request.getServiceDeliveryDays());
        post.setServiceRevisionCount(request.getServiceRevisionCount());
        post.setServiceIncludes(request.getServiceIncludes());
        post.setPortfolioUrl(request.getPortfolioUrl());
        post.setRequirements(request.getRequirements());
        post.setResponsibilities(request.getResponsibilities());
        post.setApplicationDeadline(request.getApplicationDeadline());
        post.setUpdatedAt(Instant.now());
    

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
        return deletePostById(id, PostType.JOB_REQUEST);
    }

    public String deletePostById(Integer id, PostType expectedPostType) {
        User currentUser = cUser.get();


        Post post = postRepository.findById(id).
                        orElseThrow(() -> new EntityNotFoundException("This Post does not exist"));
        ensurePostType(post, expectedPostType);

        if (!post.getCreator().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("This post does not belong to current user!");
        }

        postRepository.delete(post);



        return "Post deleted successfully";

    } 

    @Transactional
    public Page<PostDTO> getAllPosts(Pageable pageable) {
        return getAllPosts(null, null, null, null, "job_request", "newest", pageable);
    }

    @Transactional
    public Page<PostDTO> getAllPosts(String search, String category, Integer minPrice, 
                                String employmentType, String postType, String sortBy, Pageable pageable) {
        
        // Create combined specification
        var spec = PostSpecification.combineFilters(search, category, minPrice, employmentType, postType);
        
        // Handle custom sorting
        if (sortBy != null && !sortBy.isEmpty()) {
            pageable = applyCustomSort(sortBy, pageable);
        }
        
        return mapPostsPage(postRepository.findAll(spec, pageable));
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
        return getPostById(id, PostType.JOB_REQUEST);
    }

    public Post getPostById(Integer id, PostType expectedPostType){
        if (id == null) {
            throw new IllegalArgumentException("Post id cannot be null");
        }
        Post post = postRepository.findById(id).
            orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + id));
        ensurePostType(post, expectedPostType);
        return post;
    }

    public Page<Post> getPostsByCreatorId(Integer userId, Pageable pageable){
        if (userId == null){
            throw new IllegalArgumentException("User id cannot be null");
        }
        return postRepository.findByCreatorId(userId, pageable);
    }

    @Transactional
    public Page<PostDTO> getPostsByCreatorIdAsDTO(Integer userId, Pageable pageable) {
        return getPostsByCreatorIdAsDTO(userId, "job_request", pageable);
    }

    @Transactional
    public Page<PostDTO> getPostsByCreatorIdAsDTO(Integer userId, String postType, Pageable pageable) {
        if (userId == null) {
            throw new IllegalArgumentException("User id cannot be null");
        }
        var spec = PostSpecification.filterByPostType(postType)
                .and((root, query, cb) -> cb.equal(root.get("creator").get("id"), userId));
        return mapPostsPage(postRepository.findAll(spec, pageable));
    }

    public Page<Post> getMyPosts(Pageable pageable) {
        User currentUser = cUser.get();
        return getPostsByCreatorId(currentUser.getId(), pageable);
    } 

    @Transactional
    public Page<PostDTO> getMyPostsAsDTO(Pageable pageable) {
        return getMyPostsAsDTO("job_request", pageable);
    }

    @Transactional
    public Page<PostDTO> getMyPostsAsDTO(String postType, Pageable pageable) {
        User currentUser = cUser.get();
        return getPostsByCreatorIdAsDTO(currentUser.getId(), postType, pageable);
    }

    private Page<PostDTO> mapPostsPage(Page<Post> postPage) {
        List<Post> posts = postPage.getContent();
        if (posts.isEmpty()) {
            return postPage.map(post -> postMapper.toDTOForList(post, null, false, 0, Collections.emptyList()));
        }

        List<Integer> postIds = posts.stream().map(Post::getId).toList();

        Set<Integer> savedPostIds = resolveSavedPostIds(postIds);
        Map<Integer, Integer> applicationCountByPostId = resolveApplicationCounts(postIds);
        Map<Integer, List<String>> imageUrlsByPostId = resolveImageUrls(postIds);
        Map<Integer, UserDTO> posterById = resolvePosterDTOs(posts);

        return postPage.map(post -> {
            Integer creatorId = post.getCreator() != null ? post.getCreator().getId() : null;
            return postMapper.toDTOForList(
                post,
                creatorId != null ? posterById.get(creatorId) : null,
                savedPostIds.contains(post.getId()),
                applicationCountByPostId.getOrDefault(post.getId(), 0),
                imageUrlsByPostId.getOrDefault(post.getId(), Collections.emptyList())
            );
        });
    }

    private Set<Integer> resolveSavedPostIds(List<Integer> postIds) {
        try {
            User currentUser = cUser.get();
            if (currentUser == null || currentUser.getId() == null) {
                return Collections.emptySet();
            }
            return new HashSet<>(
                savedPostRepository.findSavedPostIdsByUserIdAndPostIds(currentUser.getId(), postIds)
            );
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }

    private Map<Integer, Integer> resolveApplicationCounts(List<Integer> postIds) {
        Map<Integer, Integer> countByPostId = new HashMap<>();
        for (Object[] row : jobApplicationRepository.countActiveByPostIds(postIds)) {
            Integer postId = (Integer) row[0];
            Integer count = ((Number) row[1]).intValue();
            countByPostId.put(postId, count);
        }
        return countByPostId;
    }

    private Map<Integer, List<String>> resolveImageUrls(List<Integer> postIds) {
        Map<Integer, List<String>> imageUrlsByPostId = new HashMap<>();
        for (PostImages image : postImagesRepository.findByPostIdInOrderByCreatedAtAsc(postIds)) {
            Integer postId = image.getPost() != null ? image.getPost().getId() : null;
            if (postId == null) {
                continue;
            }
            imageUrlsByPostId.computeIfAbsent(postId, k -> new ArrayList<>()).add(image.getImageUrl());
        }
        return imageUrlsByPostId;
    }

    private Map<Integer, UserDTO> resolvePosterDTOs(List<Post> posts) {
        Set<Integer> creatorIds = posts.stream()
                .map(Post::getCreator)
                .filter(creator -> creator != null && creator.getId() != null)
                .map(User::getId)
                .collect(Collectors.toSet());

        return userRepository.findAllById(creatorIds).stream()
                .collect(Collectors.toMap(User::getId, userMapper::toSummaryDTO));
    }

    private void validateImages(List<MultipartFile> images) {
        if (images == null) {
            return;
        }

        long nonEmptyImageCount = images.stream()
                .filter(image -> image != null && !image.isEmpty())
                .count();

        if (nonEmptyImageCount > 5) {
            throw new IllegalArgumentException("A post can have at most 5 images.");
        }
    }

    private void attachImages(Post post, List<MultipartFile> images) {
        if (images == null) {
            return;
        }

        List<PostImages> uploadedImages = new ArrayList<>();

        for (MultipartFile image : images) {
            if (image == null || image.isEmpty()) {
                continue;
            }

            try {
                String fileName = FileUploadService.generateFileName("post", post.getId(), "image");
                String imageUrl = fileUploadService.upload(image, fileName, "image");

                uploadedImages.add(PostImages.builder()
                        .post(post)
                        .imageUrl(imageUrl)
                        .createdAt(Instant.now())
                        .build());
            } catch (IOException e) {
                throw new IllegalStateException("Failed to upload one of the post images.", e);
            }
        }

        post.setImages(uploadedImages);
    }

    private PostType resolvePostType(PostType postType) {
        return postType == null ? PostType.JOB_REQUEST : postType;
    }

    private void ensurePostType(Post post, PostType expectedPostType) {
        PostType actualPostType = resolvePostType(post.getPostType());
        if (actualPostType != expectedPostType) {
            throw new IllegalArgumentException("Post does not belong to " + expectedPostType + " scope");
        }
    }
    
}
