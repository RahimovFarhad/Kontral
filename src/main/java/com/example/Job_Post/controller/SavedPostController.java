package com.example.Job_Post.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Job_Post.dto.PagedResponse;
import com.example.Job_Post.dto.SavedPostDTO;
import com.example.Job_Post.dto.SavedPostMapper;
import com.example.Job_Post.service.SavedPostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/saved-posts")
public class SavedPostController {
    private final SavedPostService savedPostService;
    private final SavedPostMapper savedPostMapper;


    @SuppressWarnings({ "unchecked", "rawtypes" })
    @PostMapping("/save/{postId}")
    public ResponseEntity<String> save(
        @PathVariable Integer postId
    ) {
        try {
            ResponseEntity res = ResponseEntity.ok(savedPostMapper.toDTO(savedPostService.create(postId)));
            return res;
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Post saving failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/unsave/{postId}")
    public ResponseEntity<?> delete(
        @PathVariable Integer postId
    ) {
        try {
            return ResponseEntity.ok(savedPostService.delete(postId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Post unsaving failed: " + e.getMessage());
        }


    }

    @GetMapping
    public ResponseEntity<?> getSavedPost(
        @RequestParam(name = "savedPostId") Integer id
    ) {
        try {
            return ResponseEntity.ok(savedPostMapper.toDTO(savedPostService.getSavedPostById(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Unable to get the savesPost: " + e.getMessage());
        }
    }

    @GetMapping("/mine")
    public ResponseEntity<?> getMySavedPosts(
        @PageableDefault(size = 10, sort = "savedAt") Pageable pageable
    ) {
        try {
            PagedResponse<SavedPostDTO> pagedResponse= PagedResponse.formPage(savedPostService.getMySavedPosts(pageable).map(savedPostMapper::toDTO));
            return ResponseEntity.ok(pagedResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Unable to get the savesPosts of the current user: " + e.getMessage());
        }
    }

    // @GetMapping("/user")
    // public ResponseEntity<?> getSavedPostsByUserId(
    //     @RequestParam(name = "userId") Integer userId,
    //     @PageableDefault(size = 10, sort = "savedAt") Pageable pageable

    // ){
    //     try {
    //         PagedResponse<SavedPost> pagedResponse= PagedResponse.formPage(savedPostService.getSavedPostsByUserId(userId, pageable));
    //         return ResponseEntity.ok(pagedResponse);
    //     } catch (Exception e) {   
    //         return ResponseEntity.badRequest().body("Unable to get the savesPosts by user id: " + e.getMessage());
    //     }
    // }

    // @GetMapping("/post")
    // public ResponseEntity<?> getSavedPostsByPostId(
    //     @RequestParam(name = "postId") Integer postId,
    //     @PageableDefault(size = 10, sort = "savedAt") Pageable pageable

    // ){
    //     try {
    //         PagedResponse<SavedPost> pagedResponse= PagedResponse.formPage(savedPostService.getSavedPostsByPostId(postId, pageable));
    //         return ResponseEntity.ok(pagedResponse);
    //     } catch (Exception e) {   
    //         return ResponseEntity.badRequest().body("Unable to get the savesPosts by post id: " + e.getMessage());
    //     }
    // }




    

    
}
