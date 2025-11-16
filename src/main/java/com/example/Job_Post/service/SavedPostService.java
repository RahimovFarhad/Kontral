package com.example.Job_Post.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.Job_Post.entity.Post;
import com.example.Job_Post.entity.SavedPost;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.repository.SavedPostRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SavedPostService  {
    private final SavedPostRepository savedPostRepository;

    private final PostService postService;
    private final UserService userService;


    public SavedPost create(Integer postId) {
        Post post = postService.getPostById(postId);
        User currentUser = userService.getCurrentUser();

        Optional <SavedPost> existingSavedPost = savedPostRepository.findByPostIdAndUserId(postId, currentUser.getId());

        if (existingSavedPost.isPresent()){
            throw new IllegalStateException("You have already saved this post");
        }

        SavedPost savedPost = SavedPost.builder().
                                    post(post).
                                    user(currentUser).
                                    savedAt(LocalDateTime.now()).
                                    build();

        return savedPostRepository.save(savedPost);
    }
    

    public SavedPost getSavedPostById(Integer id){
        if (id == null) 
            throw new IllegalArgumentException("SavedPost id cannot be null");
         
        return savedPostRepository.findById(id).
            orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + id));
    }

    public Page<SavedPost> getMySavedPosts(Pageable pageable){
        User currentUser = userService.getCurrentUser();

        return savedPostRepository.findByUser(currentUser, pageable);

    }

    public Page<SavedPost> getSavedPostsByUserId(Integer userId, Pageable pageable){ 
        User user = userService.getUserById(userId);

        return savedPostRepository.findByUser(user, pageable);

    }

    public Page<SavedPost> getSavedPostsByPostId(Integer postId, Pageable pageable){
        Post post = postService.getPostById(postId);

        return savedPostRepository.findByPost(post, pageable);

    }


    public String delete(Integer postId) {
        User currentUser = userService.getCurrentUser();
        SavedPost post = getSavedPostByPostIdAndUserId(postId, currentUser.getId());
        savedPostRepository.delete(post);

        return "Post unsaved successfully!";
    }


    private SavedPost getSavedPostByPostIdAndUserId(Integer postId, Integer userId) {
        return savedPostRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new IllegalArgumentException("This post is not saved!"));
        
    }



    

    


    
}
