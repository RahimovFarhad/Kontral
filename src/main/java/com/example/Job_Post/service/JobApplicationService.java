package com.example.Job_Post.service;


import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.example.Job_Post.component.CurrentUser;
import com.example.Job_Post.dto.JobApplicationDTO;
import com.example.Job_Post.dto.JobApplicationMapper;
import com.example.Job_Post.entity.ChatMessage;
import com.example.Job_Post.entity.ChatNotification;
import com.example.Job_Post.entity.JobApplication;
import com.example.Job_Post.entity.Post;
import com.example.Job_Post.entity.SalaryNegotiation;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.enumerator.JobApplicationStatus;
import com.example.Job_Post.enumerator.NegotiationStatus;
import com.example.Job_Post.enumerator.NotificationType;
import com.example.Job_Post.enumerator.SubjectType;
import com.example.Job_Post.repository.JobApplicationRepository;
import com.example.Job_Post.repository.SalaryNegotiationRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobApplicationService {
    private final JobApplicationRepository jobApplicationRepository;
    private final JobApplicationMapper jobApplicationMapper;
    private final PostService postService;
    private final NotificationService notificationService;
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    private final SalaryNegotiationRepository salaryNegotiationRepository;
    private final CurrentUser cUser;


    public JobApplicationDTO apply(JobApplicationDTO request) {        
        request.setStatus(JobApplicationStatus.APPLIED);
        User currentUser = cUser.get();

        if (!request.getCreatorDTO().getId().equals(currentUser.getId())) {
            throw new IllegalAccessError("The users don't match!");
        }

        Optional<JobApplication> existingApplication = jobApplicationRepository
                .findByPostIdAndCreatorIdAndIsWithdrawnFalse(request.getPostDTO().getId(), currentUser.getId());

        if (existingApplication.isPresent()) {
            throw new IllegalStateException("You have already applied to this job.");
        }

        JobApplication jobApplication = jobApplicationMapper.toEntity(request);
        jobApplication.setAppliedAt(LocalDateTime.now());
        jobApplication.setWithdrawn(false);
        jobApplication.setFinalSalary(jobApplication.getPost().getSalary());

        JobApplication savedJobApplication = jobApplicationRepository.save(jobApplication);

        if (savedJobApplication != null) {
            try {
                notificationService
                        .sendNotification(
                            savedJobApplication.getPost().getCreator(), 
                            NotificationType.APPLY, 
                            SubjectType.JOB_APPLICATION, 
                            savedJobApplication.getId()
                        );
            } catch (Exception e) {
                e.printStackTrace();
                // Handle the exception, maybe log it or rethrow it
                // For now, we will just ignore it
            }
            
        }

        return jobApplicationMapper.toDTO(savedJobApplication);
    }

    public JobApplication edit(JobApplicationDTO request) {
        User currentUser = cUser.get();

        JobApplication jobApplication = jobApplicationRepository.findById(request.getId()).
                        orElseThrow(() -> new EntityNotFoundException("This Job Application does not exist"));

        if (!jobApplication.getCreator().getId().equals(currentUser.getId())) {
            throw new IllegalAccessError("This jobApplication does not belong to current user!");
        }

        if(!jobApplication.getStatus().equals(JobApplicationStatus.APPLIED)){
            throw new IllegalAccessError("This application is already reviewed by the recruiter");
        }

        // Here do the editing

        JobApplication savedJobApplication = jobApplicationRepository.save(jobApplication);

        if (savedJobApplication != null) {
            try {
                notificationService
                        .sendNotification(
                            savedJobApplication.getPost().getCreator(), 
                            NotificationType.EDIT_APPLY, 
                            SubjectType.JOB_APPLICATION, 
                            savedJobApplication.getId()
                        );
            } catch (Exception e) {
                e.printStackTrace();
                // Handle the exception, maybe log it or rethrow it
                // For now, we will just ignore it
            }
            
        }


        return savedJobApplication;
    }

    public JobApplication changeStatus(Integer jobApplicationID, String status){
        User currentUser = cUser.get();

        JobApplication jobApplication = jobApplicationRepository.findById(jobApplicationID).
                        orElseThrow(() -> new EntityNotFoundException("This Job Application does not exist"));

        JobApplicationStatus jobApplicationStatus = JobApplicationStatus.valueOf(status.toUpperCase());

        NotificationType notificationType;
        User notifiedUser;
        SubjectType subjectType = SubjectType.JOB_APPLICATION;
        Integer subjectId = jobApplication.getId();

        User changer; // we will use this to check if the current user have permission of changing according to their role and what status they want to change

         switch (jobApplicationStatus) {
            case REJECTED -> {
                notificationType = NotificationType.REJECT;
                notifiedUser = jobApplication.getCreator();
                changer = jobApplication.getPost().getCreator();
            }
            case OFFERED -> {
                notificationType = NotificationType.OFFER;
                notifiedUser = jobApplication.getCreator();
                changer = jobApplication.getPost().getCreator();
            }
            case HIRED -> {
                notificationType = NotificationType.ACCEPT_OFFER;
                notifiedUser = jobApplication.getPost().getCreator();
                changer = jobApplication.getCreator();
            }
            case OFFER_REJECTED -> {
                notificationType = NotificationType.REJECT_OFFER;
                notifiedUser = jobApplication.getPost().getCreator();
                changer = jobApplication.getCreator();
            }
            case JOB_COMPLETED -> {
                notificationType = NotificationType.JOB_COMPLETED;
                notifiedUser = jobApplication.getCreator();
                changer = jobApplication.getPost().getCreator();
            }
            default -> throw new IllegalArgumentException("Unsupported JobApplicationStatus: " + jobApplicationStatus);
        }

        if (!changer.getId().equals(currentUser.getId())) {
            throw new IllegalAccessError("This user doesn't have permission to change this application!");
        }

        if (
            (jobApplicationStatus == JobApplicationStatus.OFFERED || jobApplicationStatus == JobApplicationStatus.REJECTED) && jobApplication.getStatus() != JobApplicationStatus.APPLIED ||
            (jobApplicationStatus == JobApplicationStatus.HIRED || jobApplicationStatus == JobApplicationStatus.OFFER_REJECTED) && jobApplication.getStatus() != JobApplicationStatus.OFFERED
            || (jobApplicationStatus == JobApplicationStatus.JOB_COMPLETED) && jobApplication.getStatus() != JobApplicationStatus.HIRED
        ) {
            throw new IllegalAccessError("You can't change the status from " + jobApplication.getStatus() + " to " + jobApplicationStatus);
        }

        jobApplication.setStatus(jobApplicationStatus);

       

        if (jobApplicationStatus == JobApplicationStatus.OFFERED){
            ChatMessage systemMessage = ChatMessage.builder()
                .recipient(jobApplication.getCreator())
                .sender(currentUser) // or a dedicated system user
                .content(String.format(
                    "{\"message\":\"I offer you a place. Respond to talk the details.\",\"postId\":%d,\"applicationId\":%d,\"status\":\"offered\"}", 
                    jobApplication.getPost().getId(), jobApplication.getId()))
                .isSystemGenerated(true)
                .isRead(false)
                .timestamp(LocalDateTime.now())
                .build();

            ChatMessage savedMessage = chatMessageService.saveMessage(systemMessage);


            messagingTemplate.convertAndSendToUser(
                savedMessage.getRecipient().getEmail(), 
                "/queue/messages",
                ChatNotification.builder()
                    .id(savedMessage.getId())
                    .senderId(savedMessage.getSender().getId())
                    .recipientId(savedMessage.getRecipient().getId())
                    .content(savedMessage.getContent())
                    .isSystemGenerated(savedMessage.getIsSystemGenerated())
                    .build()
            );

        }

        if (jobApplicationStatus == JobApplicationStatus.HIRED){
            ChatMessage systemMessage = ChatMessage.builder()
                .recipient(jobApplication.getPost().getCreator())
                .sender(currentUser) // or a dedicated system user
                .content(String.format(
                    "{\"message\":\"I accept your offer. Let's talk the details.\",\"postId\":%d,\"applicationId\":%d,\"status\":\"hired\"}",
                    jobApplication.getPost().getId(), jobApplication.getId()))
                .isSystemGenerated(true)
                .isRead(false)
                .timestamp(LocalDateTime.now())
                .build();

            ChatMessage savedMessage = chatMessageService.saveMessage(systemMessage);


            messagingTemplate.convertAndSendToUser(
                savedMessage.getRecipient().getEmail(), 
                "/queue/messages",
                ChatNotification.builder()
                    .id(savedMessage.getId())
                    .senderId(savedMessage.getSender().getId())
                    .recipientId(savedMessage.getRecipient().getId())
                    .content(savedMessage.getContent())
                    .isSystemGenerated(savedMessage.getIsSystemGenerated())
                    .build()
            );

        }

        if (jobApplicationStatus == JobApplicationStatus.JOB_COMPLETED){
            ChatMessage systemMessage = ChatMessage.builder()
                .recipient(jobApplication.getCreator())
                .sender(currentUser) // or a dedicated system user
                .content(String.format(
                    "{\"message\":\"Your job has been completed. Thanks for your services.\",\"postId\":%d,\"applicationId\":%d,\"status\":\"completed\"}",
                    jobApplication.getPost().getId(), jobApplication.getId()))
                .isSystemGenerated(true)
                .isRead(false)
                .timestamp(LocalDateTime.now())
                .build();

            ChatMessage savedMessage = chatMessageService.saveMessage(systemMessage);

            messagingTemplate.convertAndSendToUser(
                savedMessage.getRecipient().getEmail(),
                "/queue/messages",
                ChatNotification.builder()
                    .id(savedMessage.getId())
                    .senderId(savedMessage.getSender().getId())
                    .recipientId(savedMessage.getRecipient().getId())
                    .content(savedMessage.getContent())
                    .isSystemGenerated(savedMessage.getIsSystemGenerated())
                    .build()
            );

        }
        
        if (jobApplication != null) {
            try {
                notificationService
                        .sendNotification(
                            notifiedUser, 
                            notificationType,
                            subjectType,
                            subjectId
                        );
            } catch (Exception e) {
                e.printStackTrace();
                // Handle the exception, maybe log it or rethrow it
                // For now, we will just ignore it
            }
            
        }

        if (jobApplication.getStatus().toInt() > JobApplicationStatus.OFFERED.toInt()){
            Iterator<SalaryNegotiation> it = jobApplication.getNegotiations().iterator();
            while (it.hasNext()){
                SalaryNegotiation negotiation = it.next();
                if (negotiation.getStatus() == NegotiationStatus.ACTIVE){
                    negotiation.setStatus(NegotiationStatus.CLOSED);
                    salaryNegotiationRepository.save(negotiation);
                }
            }
        }

        return jobApplicationRepository.save(jobApplication);
    }

    public String withdrawJobApplicationById(Integer id) {
        User currentUser = cUser.get();

        JobApplication jobApplication = jobApplicationRepository.findById(id).
                        orElseThrow(() -> new EntityNotFoundException("This Post does not exist"));

        if (!jobApplication.getCreator().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("This post does not belong to current user!");
        }

        jobApplication.setWithdrawn(true);
        jobApplicationRepository.save(jobApplication);

        try {
            notificationService
                    .sendNotification(
                        jobApplication.getPost().getCreator(), 
                        NotificationType.WITHDRAW, 
                        SubjectType.USER, 
                        currentUser.getId()
                    );
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception, maybe log it or rethrow it
            // For now, we will just ignore it
        }

        return "Post withdrawn successfully";
    }


    public List<JobApplicationDTO> getMyApplications() {
        User currentUser = cUser.get();

        return jobApplicationRepository.findByCreatorAndIsWithdrawnFalse(currentUser).stream().map(application -> jobApplicationMapper.toDTO(application)).toList();


    }

    public Page<JobApplication> getApplicationsToJob(Integer jobId, Pageable pageable) {
        if (jobId == null) 
            throw new IllegalArgumentException("jobId field is null");

        Post job = postService.getPostById(jobId);
        User currentUser = cUser.get();

        if (!job.getCreator().getId().equals(currentUser.getId())) { 
            throw new AccessDeniedException("Only Job Post's creator can see the applications" );
        }


        
        return jobApplicationRepository.findByPostAndIsWithdrawnFalse(job, pageable);
    } 

    public Page<JobApplication> getApplicationsToMyJobs(Pageable pageable) {
        User currentUser = cUser.get();
        return jobApplicationRepository.findByPostCreatorIdAndIsWithdrawnFalse(currentUser.getId(), pageable);
    }

    public Integer getApplicationCount(Integer jobId) {
        if (jobId == null)
            throw new IllegalArgumentException("jobId field is null");
        
        return jobApplicationRepository.countByPostIdAndIsWithdrawnFalse(jobId);

    }

    public JobApplication getJobApplicationById(Integer id) {
        if (id == null) 
            throw new IllegalArgumentException("ID must not be null");

        User currentUser = cUser.get();
        
        JobApplication jobApplication= jobApplicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Unable to find job application by id: " + id));

                
        if (!jobApplication.getCreator().getId().equals(currentUser.getId()) && !jobApplication.getPost().getCreator().getId().equals(currentUser.getId())) { 
            throw new AccessDeniedException("Only Job Post's creator or applicant can see the applications");
        }

        return jobApplication;
    }

   





    


    
}
