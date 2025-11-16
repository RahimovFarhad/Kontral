package com.example.Job_Post.dto;
import java.time.LocalDateTime;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class SavedPostDTO {

    private Integer id;
    
    private PostDTO postDTO;
    private UserDTO userDTO;

    private LocalDateTime savedAt;

}
