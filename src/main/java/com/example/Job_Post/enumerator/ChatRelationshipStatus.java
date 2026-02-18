package com.example.Job_Post.enumerator;

public enum ChatRelationshipStatus {

    // I offered them a job, waiting for their response
    OFFER_SENT_WAITING_RESPONSE,

    // They offered me a job, I haven't responded yet
    OFFER_RECEIVED_WAITING_RESPONSE,

    // I am their employer
    EMPLOYER,

    // I am their employee
    EMPLOYEE,

    // No employment relationship
    NONE
}

