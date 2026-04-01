package com.example.Job_Post.service;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.example.Job_Post.component.CurrentUser;
import com.example.Job_Post.dto.DashboardActionItemDTO;
import com.example.Job_Post.dto.DashboardSummaryDTO;
import com.example.Job_Post.dto.EmployeeDashboardSummaryDTO;
import com.example.Job_Post.dto.EmployerDashboardSummaryDTO;
import com.example.Job_Post.entity.JobApplication;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.enumerator.JobApplicationStatus;
import com.example.Job_Post.enumerator.PreferredRole;
import com.example.Job_Post.repository.ChatMessageRepository;
import com.example.Job_Post.repository.JobApplicationRepository;
import com.example.Job_Post.repository.PostRepository;
import com.example.Job_Post.repository.SavedPostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final Set<JobApplicationStatus> ACTIVE_EMPLOYEE_APPLICATION_STATUSES =
        EnumSet.of(JobApplicationStatus.APPLIED, JobApplicationStatus.OFFERED, JobApplicationStatus.HIRED);
    private static final int TOP_ITEMS_LIMIT = 5;

    private final CurrentUser currentUser;
    private final JobApplicationRepository jobApplicationRepository;
    private final SavedPostRepository savedPostRepository;
    private final PostRepository postRepository;
    private final ChatMessageRepository chatMessageRepository;

    public DashboardSummaryDTO getSummary() {
        User user = currentUser.get();
        if (user == null || user.getId() == null) {
            throw new IllegalStateException("Authenticated user is required");
        }

        PreferredRole preferredRole = user.getPreferredRole() == null ? PreferredRole.ALL : user.getPreferredRole();
        EmployeeDashboardSummaryDTO employee = null;
        EmployerDashboardSummaryDTO employer = null;

        if (preferredRole == PreferredRole.EMPLOYEE || preferredRole == PreferredRole.ALL) {
            employee = buildEmployeeSummary(user.getId());
        }

        if (preferredRole == PreferredRole.EMPLOYER || preferredRole == PreferredRole.ALL) {
            employer = buildEmployerSummary(user.getId());
        }

        List<DashboardActionItemDTO> mergedTopItems = Stream.concat(
                employee != null ? employee.getTopActionableItems().stream() : Stream.empty(),
                employer != null ? employer.getTopActionableItems().stream() : Stream.empty()
            )
            .sorted(Comparator.comparing(DashboardActionItemDTO::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
            .limit(TOP_ITEMS_LIMIT)
            .toList();

        return DashboardSummaryDTO.builder()
            .preferredRole(preferredRole.getApiValue())
            .employee(employee)
            .employer(employer)
            .topActionableItems(mergedTopItems)
            .build();
    }

    private EmployeeDashboardSummaryDTO buildEmployeeSummary(Integer userId) {
        Integer activeApplicationsCount = safeCount(
            jobApplicationRepository.countByCreatorIdAndStatusInAndIsWithdrawnFalse(
                userId,
                ACTIVE_EMPLOYEE_APPLICATION_STATUSES
            )
        );
        Integer savedCount = safeCount(savedPostRepository.countByUserId(userId));
        Integer pendingResponsesCount = safeCount(
            jobApplicationRepository.countByCreatorIdAndStatusAndIsWithdrawnFalse(userId, JobApplicationStatus.APPLIED)
        );

        List<DashboardActionItemDTO> topItems = jobApplicationRepository
            .findTop5ByCreatorIdAndStatusInAndIsWithdrawnFalseOrderByAppliedAtDesc(
                userId,
                EnumSet.of(JobApplicationStatus.APPLIED, JobApplicationStatus.OFFERED)
            )
            .stream()
            .map(this::toEmployeeActionItem)
            .toList();

        return EmployeeDashboardSummaryDTO.builder()
            .activeApplicationsCount(activeApplicationsCount)
            .savedCount(savedCount)
            .pendingResponsesCount(pendingResponsesCount)
            .topActionableItems(topItems)
            .build();
    }

    private EmployerDashboardSummaryDTO buildEmployerSummary(Integer userId) {
        Integer activePostsCount = safeCount(postRepository.countByCreatorId(userId));
        Integer newApplicantsCount = safeCount(
            jobApplicationRepository.countByPostCreatorIdAndStatusAndIsWithdrawnFalse(userId, JobApplicationStatus.APPLIED)
        );
        Integer unreadMessagesCount = safeCount(chatMessageRepository.countByRecipientIdAndIsReadFalse(userId));

        List<DashboardActionItemDTO> topItems = jobApplicationRepository
            .findTop5ByPostCreatorIdAndStatusAndIsWithdrawnFalseOrderByAppliedAtDesc(userId, JobApplicationStatus.APPLIED)
            .stream()
            .map(this::toEmployerActionItem)
            .toList();

        return EmployerDashboardSummaryDTO.builder()
            .activePostsCount(activePostsCount)
            .newApplicantsCount(newApplicantsCount)
            .unreadMessagesCount(unreadMessagesCount)
            .topActionableItems(topItems)
            .build();
    }

    private DashboardActionItemDTO toEmployeeActionItem(JobApplication application) {
        String postTitle = application.getPost() != null ? application.getPost().getTitle() : null;
        String title;
        if (application.getStatus() == JobApplicationStatus.OFFERED) {
            title = "You received an offer";
        } else {
            title = "Awaiting employer response";
        }

        return DashboardActionItemDTO.builder()
            .type("EMPLOYEE_" + application.getStatus().name())
            .applicationId(application.getId())
            .postId(application.getPost() != null ? application.getPost().getId() : null)
            .title(title)
            .subtitle(postTitle)
            .createdAt(application.getAppliedAt())
            .build();
    }

    private DashboardActionItemDTO toEmployerActionItem(JobApplication application) {
        String applicantName = null;
        if (application.getCreator() != null) {
            applicantName = application.getCreator().getNickName() != null
                ? application.getCreator().getNickName()
                : application.getCreator().getEmail();
        }
        String postTitle = application.getPost() != null ? application.getPost().getTitle() : null;

        return DashboardActionItemDTO.builder()
            .type("EMPLOYER_NEW_APPLICANT")
            .applicationId(application.getId())
            .postId(application.getPost() != null ? application.getPost().getId() : null)
            .title("New applicant")
            .subtitle((postTitle != null ? postTitle : "") + (applicantName != null ? " - " + applicantName : ""))
            .createdAt(application.getAppliedAt())
            .build();
    }

    private Integer safeCount(Integer count) {
        return count == null ? 0 : count;
    }
}
