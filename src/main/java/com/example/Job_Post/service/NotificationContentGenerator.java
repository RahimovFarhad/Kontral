package com.example.Job_Post.service;

import org.springframework.stereotype.Service;
import com.example.Job_Post.entity.JobApplication;
import com.example.Job_Post.entity.Notification;
import com.example.Job_Post.entity.Post;
import com.example.Job_Post.entity.Review;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.repository.JobApplicationRepository;
import com.example.Job_Post.repository.PostRepository;
import com.example.Job_Post.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationContentGenerator {

    private final UserService userService;
    private final PostRepository postRepository;
    private final ReviewRepository reviewRepository;
    private final JobApplicationRepository jobApplicationRepository;

    public String generateContent(Notification notification) {
        try {
            return switch (notification.getNotificationType()) {
                case REVIEW -> generateReviewContent(notification);
                case EDIT_REVIEW -> generateEditReviewContent(notification);
                case FOLLOW -> generateFollowContent(notification);
                case APPLY -> generateApplyContent(notification);
                case WITHDRAW -> generateWithdrawContent(notification);
                case OFFER -> generateOfferContent(notification); 
                case REJECT -> generateRejectContent(notification);
                case ACCEPT_OFFER -> generateAcceptOfferContent(notification);
                case REJECT_OFFER -> generateRejectOfferContent(notification);
                case EDIT_APPLY -> generateEditApplyContent(notification);
                case EDIT_APPLIEDPOST -> generateEditAppliedPostContent(notification);
                case EDIT_SAVEDPOST -> generateEditSavedPostContent(notification);
                case DELETE_APPLIEDPOST -> generateDeleteAppliedPostContent(notification);
                case SAVE_APPLIEDPOST -> generateSaveAppliedPostContent(notification);
                case MESSAGE -> generateMessageContent(notification);
                case JOB_COMPLETED -> generateJobCompletedContent(notification);
                default -> "You have a new notification.";
            };
        } catch (Exception e) {
            log.error("Error generating notification content for notification {}: {}",
                    notification.getId(), e.getMessage(), e);
            return "You have a new notification.";
        }
    }

    private String generateJobCompletedContent(Notification notification) {
        JobApplication application = jobApplicationRepository.findById(notification.getSubjectId()).orElse(null);
        if (application == null) {
            return "A job has been completed.";
        }
        String employeeName = getDisplayName(application.getCreator());
        return String.format("%s's job has been completed", employeeName);
    }

    // ========== REVIEW ==========
    private String generateReviewContent(Notification notification) {
        Review review = reviewRepository.findById(notification.getSubjectId()).orElse(null);
        if (review == null) {
            return "You have received a new review.";
        }
        String reviewerName = getDisplayName(review.getWriter());
        return String.format("%s has left you a review", reviewerName);
    }

    private String generateEditReviewContent(Notification notification) {
        Review review = reviewRepository.findById(notification.getSubjectId()).orElse(null);
        if (review == null) {
            return "A review has been updated.";
        }
        String reviewerName = getDisplayName(review.getWriter());
        return String.format("%s has updated their review", reviewerName);
    }

    // ========== FOLLOW ==========
    private String generateFollowContent(Notification notification) {
        User follower = userService.getUserById(notification.getSubjectId());
        if (follower == null) {
            return "You have a new follower.";
        }
        String followerName = getDisplayName(follower);
        return String.format("%s is now following you", followerName);
    }

    // ========== JOB APPLICATIONS ==========
    private String generateApplyContent(Notification notification) {
        JobApplication application = jobApplicationRepository.findById(notification.getSubjectId()).orElse(null);
        if (application == null) {
            return "You have a new job application.";
        }
        String applicantName = getDisplayName(application.getCreator());
        return String.format("%s has applied for your job position", applicantName);
    }

    private String generateWithdrawContent(Notification notification) {
        User user = userService.getUserById(notification.getSubjectId());
        String userName = getDisplayName(user);
        return String.format("%s has withdrawn their job application", userName);
    }

    private String generateEditApplyContent(Notification notification) {
        JobApplication application = jobApplicationRepository.findById(notification.getSubjectId()).orElse(null);
        if (application == null) {
            return "A job application has been updated.";
        }
        String applicantName = getDisplayName(application.getCreator());
        return String.format("%s has updated their job application", applicantName);
    }

    // ========== EMPLOYER ACTIONS ==========
    private String generateOfferContent(Notification notification) {
        JobApplication application = jobApplicationRepository.findById(notification.getSubjectId()).orElse(null);
        if (application == null) {
            return "You have received a job offer.";
        }
        return String.format("You have received an offer for the job application \"%s\"",
                truncateText(application.getPost().getTitle(), 50));
    }

    private String generateRejectContent(Notification notification) {
        JobApplication application = jobApplicationRepository.findById(notification.getSubjectId()).orElse(null);
        if (application == null) {
            return "Your job application has been rejected.";
        }
        return String.format("Your application for \"%s\" has been rejected",
                truncateText(application.getPost().getTitle(), 50));
    }

    private String generateAcceptOfferContent(Notification notification) {
        JobApplication application = jobApplicationRepository.findById(notification.getSubjectId()).orElse(null);
        if (application == null) {
            return "Your job offer has been accepted.";
        }
        return String.format("%s has accepted your job offer", application.getCreator().getUsername());
    }

    private String generateRejectOfferContent(Notification notification) {
        JobApplication application = jobApplicationRepository.findById(notification.getSubjectId()).orElse(null);
        if (application == null) {
            return "Your job offer has been rejected.";
        }
        return String.format("%s has rejected your job offer", application.getCreator().getUsername());
    }

    // ========== POST UPDATES ==========
    private String generateEditAppliedPostContent(Notification notification) {
        Post post = postRepository.findById(notification.getSubjectId()).orElse(null);
        if (post == null) {
            return "A job you applied to has been updated.";
        }
        return String.format("Job posting \"%s\" has been updated",
                truncateText(post.getTitle(), 50));
    }

    private String generateEditSavedPostContent(Notification notification) {
        Post post = postRepository.findById(notification.getSubjectId()).orElse(null);
        if (post == null) {
            return "A saved job has been updated.";
        }
        return String.format("Saved job \"%s\" has been updated",
                truncateText(post.getTitle(), 50));
    }

    private String generateDeleteAppliedPostContent(Notification notification) {
        Post post = postRepository.findById(notification.getSubjectId()).orElse(null);
        if (post == null) {
            return "A job you applied to has been deleted.";
        }
        return String.format("Job posting \"%s\" you applied to has been deleted",
                truncateText(post.getTitle(), 50));
    }

    private String generateSaveAppliedPostContent(Notification notification) {
        Post post = postRepository.findById(notification.getSubjectId()).orElse(null);
        if (post == null) {
            return "A job you applied to has been saved.";
        }
        return String.format("Job posting \"%s\" you applied to has been saved",
                truncateText(post.getTitle(), 50));
    }

    // ========== MESSAGES ==========
    private String generateMessageContent(Notification notification) {
        User sender = userService.getUserById(notification.getSubjectId());
        String senderName = getDisplayName(sender);
        return String.format("New message from %s", senderName);
    }

    // ========== HELPERS ==========
    private String getDisplayName(User user) {
        if (user == null) {
            return "Unknown User";
        }
        if (user.getNickName() != null && !user.getNickName().trim().isEmpty()) {
            return user.getNickName();
        }
        if (user.getEmail() != null) {
            return user.getEmail().split("@")[0]; // Use part before @
        }
        return "User #" + user.getId();
    }

    private String truncateText(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
}
