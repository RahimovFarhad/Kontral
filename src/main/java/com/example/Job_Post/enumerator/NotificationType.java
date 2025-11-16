package com.example.Job_Post.enumerator;

import org.springframework.boot.autoconfigure.batch.BatchProperties.Job;

public enum NotificationType {
    REVIEW, // handled at service layer

    EDIT_REVIEW, //you get this notification when someone edits their review ; handled at service layer

    FOLLOW,

    APPLY, //you get this notification when someone applies to your job; handled at service layer

    WITHDRAW, //you get this notification when someone who applied to your job removes their application; //handled at service layer

    OFFER, // applicant gets this when post creator accepts their application and makes offer

    REJECT, // applicant gets this when post creator reject their application and makes offer

    ACCEPT_OFFER, //post creator (employer) gets this when applicant, who was offered a position, accepts the offer 

    REJECT_OFFER, //post creator (employer) gets this when applicant, who was offered a position, rejects the offer 

    EDIT_APPLY, //you get this notification when someone who applied to your job edits their application; //handled at service layer

    EDIT_APPLIEDPOST, //you get notification when some changes happen in the job you applied; // handled at service layer

    EDIT_SAVEDPOST, //you get notification when some changes happen in the job you saved; // handled at service layer

    DELETE_APPLIEDPOST,

    SAVE_APPLIEDPOST,

    MESSAGE, //someoneSentMessageToYou

    JOB_COMPLETED; // employee gets this when employer marks the job as completed successfully

    @Override
    public String toString() {
        return name().toLowerCase(); // or name() if you want uppercase strings
    }
}
