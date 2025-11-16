package com.example.Job_Post.dto;

import com.example.Job_Post.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ChatUserDTO extends UserDTO{
    private Boolean hasUnseenMessageToCurrentUser;



}
