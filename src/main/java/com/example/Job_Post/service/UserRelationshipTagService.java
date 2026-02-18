package com.example.Job_Post.service;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.Job_Post.entity.JobApplication;
import com.example.Job_Post.entity.UserRelationshipTag;
import com.example.Job_Post.enumerator.ChatRelationshipStatus;
import com.example.Job_Post.enumerator.JobApplicationStatus;
import com.example.Job_Post.repository.JobApplicationRepository;
import com.example.Job_Post.repository.UserRelationshipTagRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserRelationshipTagService {

    private final UserRelationshipTagRepository userRelationshipTagRepository;
    private final JobApplicationRepository jobApplicationRepository;

    public void upsertFromJobApplication(JobApplication jobApplication) {
        Integer applicantId = jobApplication.getCreator().getId();
        Integer employerId = jobApplication.getPost().getCreator().getId();
        Pair pair = pair(applicantId, employerId);

        UserRelationshipTag tag = userRelationshipTagRepository
            .findByUserLowIdAndUserHighId(pair.low(), pair.high())
            .orElseGet(() -> UserRelationshipTag.builder()
                .userLowId(pair.low())
                .userHighId(pair.high())
                .build());

        tag.setLastApplicationId(jobApplication.getId());
        tag.setLastApplicationStatus(jobApplication.getStatus());
        tag.setApplicantId(applicantId);
        tag.setUpdatedAt(Instant.now());
        userRelationshipTagRepository.save(tag);
    }

    public void refreshPairFromLatestActive(Integer userAId, Integer userBId) {
        List<JobApplication> latest = jobApplicationRepository.findActiveBetweenUsersOrdered(
            userAId,
            userBId,
            PageRequest.of(0, 1)
        );

        Pair pair = pair(userAId, userBId);
        if (latest.isEmpty()) {
            userRelationshipTagRepository
                .findByUserLowIdAndUserHighId(pair.low(), pair.high())
                .ifPresent(userRelationshipTagRepository::delete);
            return;
        }

        upsertFromJobApplication(latest.get(0));
    }

    public Map<Integer, ChatRelationshipStatus> getRelationshipMapForCurrentUser(
        Integer currentUserId,
        Collection<Integer> otherUserIds
    ) {
        if (otherUserIds == null || otherUserIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<UserRelationshipTag> tags =
            userRelationshipTagRepository.findForCurrentUserAndOtherUsers(currentUserId, otherUserIds);

        Map<Integer, ChatRelationshipStatus> relationshipByOtherUserId = new HashMap<>();
        for (UserRelationshipTag tag : tags) {
            Integer otherUserId = tag.getUserLowId().equals(currentUserId)
                ? tag.getUserHighId()
                : tag.getUserLowId();

            relationshipByOtherUserId.put(
                otherUserId,
                mapToChatRelationship(currentUserId, tag.getApplicantId(), tag.getLastApplicationStatus())
            );
        }

        return relationshipByOtherUserId;
    }

    private ChatRelationshipStatus mapToChatRelationship(
        Integer currentUserId,
        Integer applicantId,
        JobApplicationStatus status
    ) {
        if (status == null || applicantId == null || currentUserId == null) {
            return ChatRelationshipStatus.NONE;
        }
        System.out.println("\n" + status + "\n");

        return switch (status) {
            case OFFERED -> currentUserId.equals(applicantId)
                ? ChatRelationshipStatus.OFFER_RECEIVED_WAITING_RESPONSE
                : ChatRelationshipStatus.OFFER_SENT_WAITING_RESPONSE;
            case HIRED -> currentUserId.equals(applicantId)
                ? ChatRelationshipStatus.EMPLOYEE
                : ChatRelationshipStatus.EMPLOYER;
            default -> ChatRelationshipStatus.NONE;
        };
    }

    private Pair pair(Integer a, Integer b) {
        if (a <= b) {
            return new Pair(a, b);
        }
        return new Pair(b, a);
    }

    private record Pair(Integer low, Integer high) {}
}
