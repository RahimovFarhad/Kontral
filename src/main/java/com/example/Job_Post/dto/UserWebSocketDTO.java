package com.example.Job_Post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.example.Job_Post.enumerator.Status;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWebSocketDTO {
  private Integer id;
  private String email;
  private String nickName;
  private Status status;
}