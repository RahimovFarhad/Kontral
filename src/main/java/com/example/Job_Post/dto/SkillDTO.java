package com.example.Job_Post.dto;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class SkillDTO {

    private Integer id;
    
    private String skillType;
    private String name;
    private int level;
    private String experience;

}
