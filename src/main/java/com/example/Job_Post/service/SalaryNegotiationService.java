package com.example.Job_Post.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.Job_Post.dto.SalaryNegotiationDTO;
import com.example.Job_Post.dto.SalaryNegotiationMapper;
import com.example.Job_Post.entity.JobApplication;
import com.example.Job_Post.entity.SalaryNegotiation;
import com.example.Job_Post.entity.SalaryOffer;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.enumerator.JobApplicationStatus;
import com.example.Job_Post.enumerator.NegotiationStatus;
import com.example.Job_Post.repository.JobApplicationRepository;
import com.example.Job_Post.repository.SalaryNegotiationRepository;
import com.example.Job_Post.repository.SalaryOfferRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional // maybe would cause issues later, be careful
public class SalaryNegotiationService {
    private final SalaryNegotiationRepository salaryNegotiationRepository;
    private final SalaryOfferRepository salaryOfferRepository;
    private final SalaryNegotiationMapper salaryNegotiationMapper;
    private final UserService userService;
    private final JobApplicationRepository jobApplicationRepository;

    public SalaryNegotiation initiateSalaryNegotiation(Integer jobApplicationId) {

        JobApplication jobApp = jobApplicationRepository.findById(jobApplicationId)
                .orElseThrow(() -> new IllegalArgumentException("Job application not found"));

        if (jobApp.getPost().getIsNegotiable() == null || !jobApp.getPost().getIsNegotiable()) {
            throw new IllegalArgumentException("Salary negotiation is not allowed for this job post");
        }
        
        SalaryNegotiation existingNegotiation = salaryNegotiationRepository.findByJobApplicationIdAndStatus(jobApplicationId, NegotiationStatus.ACTIVE);
        if (existingNegotiation != null) {
            return existingNegotiation;
        }
        
        SalaryNegotiation negotiation = new SalaryNegotiation();
        negotiation.setStatus(NegotiationStatus.ACTIVE);
        negotiation.setJobApplication(jobApp);

        User currentUser = userService.getCurrentUser(); // Implement this method to get the current user
        
        User applicant = jobApp.getCreator(); // or jobApp.getApplicant() if renamed
        if (!currentUser.getId().equals(applicant.getId())) {
            throw new IllegalArgumentException("Only the applicant can initiate negotiation");
        }
        negotiation.setInitiator(currentUser);
        
        if (jobApp.getStatus() != null && !jobApp.getStatus().equals(JobApplicationStatus.OFFERED)) {
            throw new IllegalArgumentException("Negotiation can only be initiated for offered applications");
        }
        if (!negotiation.getInitiator().getId().equals(jobApp.getCreator().getId())) {
            throw new IllegalArgumentException("Only the applicant can initiate negotiation");
        }
        if (!jobApp.getStatus().equals(JobApplicationStatus.OFFERED)){
            throw new IllegalArgumentException("Salary negotiation can only be made during offer stage");
        }



        return salaryNegotiationRepository.save(negotiation);
    }

    public SalaryOffer makeOffer(Integer negotiationId, Double newProposedSalary, String message) {
        SalaryNegotiation negotiation = salaryNegotiationRepository.findById(negotiationId)
                .orElseThrow(() -> new IllegalArgumentException("Negotiation not found with id: " + negotiationId));

        if (!negotiation.getStatus().equals(NegotiationStatus.ACTIVE)) {
            throw new IllegalArgumentException("Cannot make offer in a non-active negotiation");
        }
   

        User currentUser = userService.getCurrentUser(); // Implement this method to get the current user

        JobApplication jobApp = negotiation.getJobApplication();
        List<SalaryOffer> offers = negotiation.getOffers();
        
        if (!jobApp.getCreator().getId().equals(currentUser.getId()) &&
            !jobApp.getPost().getCreator().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You are not authorized to make offer in this negotiation");
        } else if (offers != null && offers.size() > 0 && offers.getLast() != null &&
                   offers.getLast().getSender().getId().equals(currentUser.getId()) && !offers.getLast().isResponded() ) {
            throw new IllegalArgumentException("You cannot make consecutive offers without a response");
        } else if (offers.size() == 0 && !negotiation.getInitiator().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Only the initiator can make the first offer");
        }

        if (!jobApp.getStatus().equals(JobApplicationStatus.OFFERED)){
            throw new IllegalArgumentException("Salary negotiation can only be made during offer stage");
        }
        

        SalaryOffer offer = SalaryOffer.builder()
                .negotiation(negotiation)
                .sender(currentUser)
                .proposedSalary(newProposedSalary)
                .message(message)
                .accepted(false)
                .isResponded(false)
                .build();

        return salaryOfferRepository.save(offer);
    }

    // Also add the functionality to actually change the money on the JobApplication when accepted
    public SalaryOffer acceptSalaryOffer(Integer offerId) {
        SalaryOffer offer = salaryOfferRepository.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Offer not found with id: " + offerId));
        
        User currentUser = userService.getCurrentUser();
        JobApplication jobApp = offer.getNegotiation().getJobApplication();
        if (!jobApp.getCreator().getId().equals(currentUser.getId()) &&
            !jobApp.getPost().getCreator().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You are not authorized to make changes in this negotiation");
        } else if (offer.getSender().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You cannot accept or reject your own offer");
        }

        if (offer.isResponded()) {
            throw new IllegalArgumentException("This offer has already been responded to");
        }

        offer.setAccepted(true);
        offer.setResponded(true);

        SalaryNegotiation negotiation = offer.getNegotiation();
        negotiation.setStatus(NegotiationStatus.ACCEPTED);
        salaryNegotiationRepository.save(negotiation);

        jobApp.setFinalSalary(offer.getProposedSalary());
        jobApplicationRepository.save(jobApp);

        return salaryOfferRepository.save(offer);
    }

    public SalaryOffer rejectSalaryOffer(Integer offerId) {

        SalaryOffer offer = salaryOfferRepository.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Offer not found with id: " + offerId));

        User currentUser = userService.getCurrentUser();
        JobApplication jobApp = offer.getNegotiation().getJobApplication();
        if (!jobApp.getCreator().getId().equals(currentUser.getId()) &&
            !jobApp.getPost().getCreator().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You are not authorized to make changes in this negotiation");
        } else if (offer.getSender().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You cannot accept or reject your own offer");
        }

        if (offer.isResponded()) {
            throw new IllegalArgumentException("This offer has already been responded to");
        }

        offer.setAccepted(false);
        offer.setResponded(true);

        return salaryOfferRepository.save(offer);
    }


    public SalaryNegotiationDTO getNegotiationDetails(Integer negotiationId) {
        SalaryNegotiation negotiation = salaryNegotiationRepository.findById(negotiationId)
                .orElseThrow(() -> new IllegalArgumentException("Negotiation not found with id: " + negotiationId));

        User currentUser = userService.getCurrentUser();
        JobApplication jobApp = negotiation.getJobApplication();
        if (!jobApp.getCreator().getId().equals(currentUser.getId()) &&
            !jobApp.getPost().getCreator().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You are not authorized to view this negotiation");
        }

        return salaryNegotiationMapper.toDTO(negotiation);
    }

    public List<SalaryNegotiationDTO> getAllNegotiations(Integer jobApplicationId) {
        List<SalaryNegotiation> negotiations = salaryNegotiationRepository.findByJobApplicationId(jobApplicationId);
        return negotiations.stream()
                .map(salaryNegotiationMapper::toDTO)
                .collect(Collectors.toList());
    }

    public SalaryOffer getLastOffer(Integer jobApplicationId) {
        SalaryOffer offer = salaryOfferRepository.findFirstByNegotiationJobApplicationIdOrderByIdDesc(jobApplicationId);
        if (offer == null) {
            throw new IllegalArgumentException("No offers found for job application id: " + jobApplicationId);
        }
        return offer;
    }


}
