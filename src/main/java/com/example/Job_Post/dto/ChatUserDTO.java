package com.example.Job_Post.dto;

import com.example.Job_Post.enumerator.Status;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatUserDTO {

    private Integer id;
    private String nickName;
    private String imageUrl;   // match entity field exactly
    private String email;     // MUST be Status enum, NOT String
    private Status status;     // MUST be Status enum, NOT String

    private boolean hasUnseenMessageToCurrentUser;

    public ChatUserDTO(Integer id, String nickName, String imageUrl, Status status, String email) {
        this.id = id;
        this.nickName = (nickName != null) ? nickName : email;
        this.imageUrl = imageUrl;
        this.status = status;
        this.email = email;
    }
}
