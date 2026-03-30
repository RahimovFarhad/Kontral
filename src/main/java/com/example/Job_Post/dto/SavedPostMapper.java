package com.example.Job_Post.dto;

import java.util.Collections;

import org.springframework.stereotype.Component;

import com.example.Job_Post.entity.SavedPost;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SavedPostMapper {
    private final UserMapper userMapper;
    private final PostMapper postMapper;

    public SavedPost toEntity(SavedPostDTO savedPostDTO) {
        if (savedPostDTO == null) {
            return null;
        }
        return SavedPost.builder()
                .id(savedPostDTO.getId())
                .post(postMapper.toEntity(savedPostDTO.getPostDTO()))
                .user(userMapper.toEntity(savedPostDTO.getUserDTO()))
                .savedAt(savedPostDTO.getSavedAt())
                .build();
    }

    // Example method to convert entity to DTO
    public SavedPostDTO toDTO(SavedPost savedPost) {
        if (savedPost == null) {
            return null;
        }
        return SavedPostDTO.builder()
                .id(savedPost.getId())
                .postDTO(postMapper.toDTO(savedPost.getPost()))
                .userDTO(userMapper.toDTO(savedPost.getUser()))
                .savedAt(savedPost.getSavedAt())
                .build();
    }

    public SavedPostDTO toListDTO(SavedPost savedPost) {
        if (savedPost == null) {
            return null;
        }

        return SavedPostDTO.builder()
                .id(savedPost.getId())
                .postDTO(postMapper.toDTOForList(
                        savedPost.getPost(),
                        userMapper.toSummaryDTO(savedPost.getPost() != null ? savedPost.getPost().getCreator() : null),
                        true,
                        0,
                        Collections.emptyList()
                ))
                .userDTO(userMapper.toSummaryDTO(savedPost.getUser()))
                .savedAt(savedPost.getSavedAt())
                .build();
    }
    
}
