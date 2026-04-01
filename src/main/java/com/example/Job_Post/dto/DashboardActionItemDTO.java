package com.example.Job_Post.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardActionItemDTO {
    private String type;
    private Integer applicationId;
    private Integer postId;
    private String title;
    private String subtitle;
    private Instant createdAt;
}
