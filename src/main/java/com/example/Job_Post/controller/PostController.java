package com.example.Job_Post.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Job_Post.dto.PagedResponse;
import com.example.Job_Post.dto.PostDTO;
import com.example.Job_Post.dto.PostMapper;
import com.example.Job_Post.service.PostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostMapper postMapper;


    @SuppressWarnings({ "unchecked", "rawtypes" })
    @PostMapping("/create")
    public ResponseEntity<String> createPost(@RequestBody PostDTO request) {
        try {
            ResponseEntity res = ResponseEntity.ok(postMapper.toDTO(postService.create(request)));
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Post creation failed: " + e.getMessage());
        }


    }

    @SuppressWarnings("unchecked")
    @PutMapping("/edit")
    public ResponseEntity<String> editPost(@RequestBody PostDTO request){
        try {
            @SuppressWarnings("rawtypes")
            ResponseEntity res = ResponseEntity.ok(postService.edit(request));
            return res;
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Post edit failed: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePostById(@PathVariable Integer id){
        try {
            @SuppressWarnings("rawtypes")
            ResponseEntity res = ResponseEntity.ok(postService.deletePostById(id)); 
            return res;
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Cannot delete post with id " + id + " : " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllPosts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) String employmentType,
            @RequestParam(required = false, defaultValue = "newest") String sortBy,
            @PageableDefault(size = 10) Pageable pageable,
            Principal principal
    ) {
        try {            
            Page<PostDTO> page = postService.getAllPosts(
                search, category, minPrice, maxPrice, employmentType, sortBy, pageable
            );
            
            PagedResponse<PostDTO> response = PagedResponse.formPage(page);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Cannot get posts: " + e.getMessage());
        }
    }

    @GetMapping("/mine")
    public ResponseEntity<?> getAllMyPosts(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable
    ) {
        try {            
            Page<PostDTO> page = postService.getMyPosts(pageable).map(post -> postMapper.toDTO(post));
            PagedResponse<PostDTO> response = PagedResponse.formPage(page);
            
            return ResponseEntity.ok(response);   
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Cannot get your posts: " + e.getMessage());
        }
    }


    @SuppressWarnings("unchecked")
    @GetMapping("/{id}")
    public ResponseEntity<String> getPostById(@PathVariable Integer id){
        try {
            @SuppressWarnings("rawtypes")
            ResponseEntity res = ResponseEntity.ok(postMapper.toDTO(postService.getPostById(id)));
            return res;
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Cannot get post with id " + id + " : " + e.getMessage());
        }

    }

    // @GetMapping()
    // @ResponseBody
    // public ResponseEntity<?> getPostByUserId(
    //     @RequestParam Integer userId,
    //     @PageableDefault(size = 10, sort = "createdAt") Pageable pageable
    // ){
    //     try {
    //         Page<PostDTO> page = postService.getPostsByCreatorId(userId, pageable).map(postMapper::toDTO);
    //         PagedResponse<PostDTO> response = PagedResponse.formPage(page);

    //         return ResponseEntity.ok(response);
    //     } catch (Exception e) {
    //         return ResponseEntity.badRequest().body("Cannot get post with user id " + userId + " : " + e.getMessage());
    //     }

    // }




    
}
