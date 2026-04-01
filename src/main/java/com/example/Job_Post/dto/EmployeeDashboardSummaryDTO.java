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
public class EmployeeDashboardSummaryDTO {
    private Integer activeApplicationsCount;
    private Integer savedCount;
    private Integer pendingResponsesCount;
    private List<DashboardActionItemDTO> topActionableItems;
}
