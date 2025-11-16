package com.example.Job_Post.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Job_Post.dto.SalaryNegotiationMapper;
import com.example.Job_Post.dto.SalaryOfferMapper;
import com.example.Job_Post.service.SalaryNegotiationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/salary-negotiations")
@RequiredArgsConstructor
public class SalaryNegotiationController {
    private final SalaryNegotiationService salaryNegotiationService;
    private final SalaryNegotiationMapper salaryNegotiationMapper;
    private final SalaryOfferMapper salaryOfferMapper;

    @PostMapping()
    public ResponseEntity<?> initiateNegotiation(@RequestParam("jobApplicationId") Integer jobApplicationId) {
        try {
            return ResponseEntity.ok(salaryNegotiationMapper.toDTO(salaryNegotiationService.initiateSalaryNegotiation(jobApplicationId)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error initiating negotiation: " + e.getMessage());
        }

    }

    @PostMapping("/{negotiationId}/offers")
    public ResponseEntity<?> makeOffer(@PathVariable Integer negotiationId, @RequestParam Double newProposedSalary, @RequestParam String message) {
        try {
            return ResponseEntity.ok(salaryOfferMapper.toDTO(salaryNegotiationService.makeOffer(negotiationId, newProposedSalary, message)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error making offer: " + e.getMessage());
        }
    }

    @PostMapping("/offers/{id}/accept")
    public ResponseEntity<?> acceptOffer(@PathVariable("id") Integer offerId) {
        try {
            return ResponseEntity.ok(salaryOfferMapper.toDTO(salaryNegotiationService.acceptSalaryOffer(offerId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error accepting offer: " + e.getMessage());
        }
    }

    @PostMapping("/offers/{id}/reject")
    public ResponseEntity<?> rejectOffer(@PathVariable("id") Integer offerId) {
        try {
            return ResponseEntity.ok(salaryOfferMapper.toDTO(salaryNegotiationService.rejectSalaryOffer(offerId)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error rejecting offer: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNegotiationDetails(@PathVariable("id") Integer negotiationId) {
        try {
            return ResponseEntity.ok(salaryNegotiationService.getNegotiationDetails(negotiationId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching negotiation details: " + e.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<?> getAllNegotiations(@RequestParam("jobApplicationId") Integer jobApplicationId) {
        try {
            return ResponseEntity.ok(salaryNegotiationService.getAllNegotiations(jobApplicationId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error fetching negotiations: " + e.getMessage());
        }
    }

    @GetMapping("/offers/last")
    public ResponseEntity<?> getLastOffer(@RequestParam("jobApplicationId") Integer jobApplicationId) {
        try {
            return ResponseEntity.ok(salaryOfferMapper.toDTO(salaryNegotiationService.getLastOffer(jobApplicationId)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error fetching last offer: " + e.getMessage());
        }
    }
}
