package com.example.Job_Post.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CompanyDTO extends UserDTO {
    private String companyName;
    private String website;
    private String description;

}
    

