package com.example.Job_Post.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Job_Post.dto.JobApplicationDTO;
import com.example.Job_Post.dto.JobApplicationMapper;
import com.example.Job_Post.dto.PagedResponse;
import com.example.Job_Post.service.JobApplicationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/job-applications")
// This controller will handle job application related endpoints
public class JobApplicationController {
    private final JobApplicationService jobApplicationService;
    private final JobApplicationMapper jobApplicationMapper;


    @SuppressWarnings({  "rawtypes" })
    @PostMapping("/apply")
    public ResponseEntity<?> applyJob(@RequestBody JobApplicationDTO request) {
        try {
            ResponseEntity res = ResponseEntity.ok(jobApplicationService.apply(request));
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Job application failed: " + e.getMessage());
        }
    }


    @PutMapping("/withdraw/{applicationId}")
    public ResponseEntity<?> withdrawApplication(@PathVariable Integer applicationId) {
        try {
            ResponseEntity<?> res = ResponseEntity.ok(jobApplicationService.withdrawJobApplicationById(applicationId));
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Job application failed: " + e.getMessage());
        }
    }
    
    
    @SuppressWarnings({  "rawtypes" })
    @GetMapping("/my-applications")
    public ResponseEntity<?> getMyJobApplications(){
        try {
            ResponseEntity res = ResponseEntity.ok(jobApplicationService.getMyApplications());
            return res;
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Unable to find user's applications: " + e.getMessage());

        }
    }

    @GetMapping("/get-applications-to-job/{jobId}")
    public ResponseEntity<?> getApplicationsToMyJob(
        @PathVariable Integer jobId,
        @PageableDefault(size = 10, sort = "appliedAt") Pageable pageable
    ){
        try {
            Page<JobApplicationDTO> page = jobApplicationService.getApplicationsToJob(jobId, pageable).map(jobApplicationMapper::toDTO);
            PagedResponse<JobApplicationDTO> res = PagedResponse.formPage(page);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Unable to find user's applications: " + e.getMessage());

        }
        
    }
    @GetMapping("/get-applications-to-my-jobs")
    public ResponseEntity<?> getApplicationsToMyJobS(
        @PageableDefault(size = 10, sort = "appliedAt") Pageable pageable
    ){
        try {
            Page<JobApplicationDTO> page = jobApplicationService.getApplicationsToMyJobs(pageable).map(jobApplicationMapper::toDTO);
            PagedResponse<JobApplicationDTO> res = PagedResponse.formPage(page);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Unable to find user's applications: " + e.getMessage());

        }
        
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<?> getApplication(@PathVariable Integer applicationId){
        try {
            JobApplicationDTO jobApplicationDTO = jobApplicationMapper.toDTO( jobApplicationService.getJobApplicationById(applicationId) );
            return ResponseEntity.ok(jobApplicationDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Unable to find job application: " + e.getMessage());

        }
    }


    @GetMapping("/count")
    public ResponseEntity<?> getApplicationCount(@RequestParam(name = "jobId") Integer jobId){
        try {
            return ResponseEntity.ok(jobApplicationService.getApplicationCount(jobId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Unable to find job application count: " + e.getMessage());
        }

    }

    @PutMapping("/change/{applicationId}/{applicationStatus}")
    public ResponseEntity<?> changeApplicationStatus(
        @PathVariable(name = "applicationId") Integer id,
        @PathVariable(name = "applicationStatus") String status
    ){
        try {
            return ResponseEntity.ok(jobApplicationMapper.toDTO(jobApplicationService.changeStatus(id, status)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Unable to change status: " + e.getMessage());
        }

    }
    // Add job application status changer 
}
