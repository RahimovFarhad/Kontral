package com.example.Job_Post.enumerator;

public enum JobApplicationStatus {
    APPLIED,           // The candidate submitted the application
    // UNDER_REVIEW,      // Recruiter has seen the application
    // INTERVIEW_SCHEDULED, // Interview is arranged
    // INTERVIEWED,       // Candidate was interviewed
    OFFERED,           // Candidate has received a job offer
    REJECTED,          // Candidate was rejected
    HIRED,              // Candidate accepted and was hired
    OFFER_REJECTED,     // Candidate rejected the job offer
    JOB_COMPLETED;      // Job has been completed successfully


    @Override
    public String toString() {
        return name().toLowerCase(); // or name() if you want uppercase strings
    }

    public int toInt() {
        return this.ordinal();
    }
}
