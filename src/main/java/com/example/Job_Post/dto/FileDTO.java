package com.example.Job_Post.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class FileDTO {
    private Integer id;
    private String name;
    private String url;
    private String type;
    private Double size;

    private Boolean isActive;

    private LocalDateTime uploadedAt;
}
