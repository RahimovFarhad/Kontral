package com.example.Job_Post.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployerDashboardSummaryDTO {
    private Integer activePostsCount;
    private Integer newApplicantsCount;
    private Integer unreadMessagesCount;
    private List<DashboardActionItemDTO> topActionableItems;
}
