package com.example.Job_Post.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.Job_Post.dto.PagedResponse;
import com.example.Job_Post.dto.PostDTO;
import com.example.Job_Post.dto.PostMapper;
import com.example.Job_Post.enumerator.PostType;
import com.example.Job_Post.service.PostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/service-posts")
@RequiredArgsConstructor
public class ServicePostController {
    private final PostService postService;
    private final PostMapper postMapper;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createServicePost(@RequestBody PostDTO request) {
        try {
            ResponseEntity res = ResponseEntity.ok(
                    postMapper.toDTO(postService.create(request, null, PostType.SERVICE_OFFER)));
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Service post creation failed: " + e.getMessage());
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createServicePostWithImages(
            @RequestPart("post") PostDTO request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        try {
            ResponseEntity res = ResponseEntity.ok(
                    postMapper.toDTO(postService.create(request, images, PostType.SERVICE_OFFER)));
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Service post creation failed: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    @PutMapping("/edit")
    public ResponseEntity<String> editServicePost(@RequestBody PostDTO request) {
        try {
            @SuppressWarnings("rawtypes")
            ResponseEntity res = ResponseEntity.ok(postService.edit(request, PostType.SERVICE_OFFER));
            return res;
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Service post edit failed: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteServicePostById(@PathVariable Integer id) {
        try {
            @SuppressWarnings("rawtypes")
            ResponseEntity res = ResponseEntity.ok(postService.deletePostById(id, PostType.SERVICE_OFFER));
            return res;
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Cannot delete service post with id " + id + " : " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllServicePosts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) String employmentType,
            @RequestParam(required = false, defaultValue = "newest") String sortBy,
            @PageableDefault(size = 10) Pageable pageable) {
        try {
            Page<PostDTO> page = postService.getAllPosts(
                    search, category, minPrice, employmentType, PostType.SERVICE_OFFER.toString(), sortBy, pageable);
            PagedResponse<PostDTO> response = PagedResponse.formPage(page);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Cannot get service posts: " + e.getMessage());
        }
    }

    @GetMapping("/mine")
    public ResponseEntity<?> getAllMyServicePosts(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        try {
            Page<PostDTO> page = postService.getMyPostsAsDTO(PostType.SERVICE_OFFER.toString(), pageable);
            PagedResponse<PostDTO> response = PagedResponse.formPage(page);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Cannot get your service posts: " + e.getMessage());
        }
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<?> getServicePostsByCreatorId(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable,
            @PathVariable Integer userId) {
        try {
            Page<PostDTO> page = postService.getPostsByCreatorIdAsDTO(userId, PostType.SERVICE_OFFER.toString(), pageable);
            PagedResponse<PostDTO> response = PagedResponse.formPage(page);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Cannot get service posts of user " + userId + ":" + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/{id}")
    public ResponseEntity<String> getServicePostById(@PathVariable Integer id) {
        try {
            @SuppressWarnings("rawtypes")
            ResponseEntity res = ResponseEntity.ok(postMapper.toDTO(postService.getPostById(id, PostType.SERVICE_OFFER)));
            return res;
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Cannot get service post with id " + id + " : " + e.getMessage());
        }
    }
}
