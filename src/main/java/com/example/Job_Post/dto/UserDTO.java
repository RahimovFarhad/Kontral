package com.example.Job_Post.dto;

import java.time.Instant;
import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer id;
    private String nickName;
    private String email;
    private String status; // "ONLINE", "OFFLINE"
    private String aboutMe; // "ONLINE", "OFFLINE"
    private String number;

    private String firstName;
    private String lastName;
    private String contactNumber;       

    private String profileImage;

    private Double averageRating;
    private Instant createdAt;
    private Instant updatedAt;

    private List<SkillDTO> skills; // List of skills with endorsements
    private List<FileDTO> files; // List of uploaded files

    private Integer newNotificationCount;
    private Integer newChatMessageCount;
    private LatestUnreadActionNotificationDTO latestUnreadActionNotification;

    private String linkedIn;
    private String preferredRole;
    
    @Builder.Default
    private Boolean isCompany = false;
    private Boolean isFirstTimeAccessing;
    private Boolean isAccountComplete;

}
