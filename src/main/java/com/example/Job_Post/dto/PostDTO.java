package com.example.Job_Post.dto;

import java.time.Instant;
import java.util.List;

import com.example.Job_Post.dto.deserializer.NullableDoubleDeserializer;
import com.example.Job_Post.dto.deserializer.NullableIntegerDeserializer;
import com.example.Job_Post.enumerator.PostType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private Integer id;
    private UserDTO poster;

    private String title; 
    private String description; 

    private Boolean isCompany;
    private String companyName; 

    private String location;

    private String employmentType;
    private String category; 
    private PostType postType;

    @JsonDeserialize(using = NullableDoubleDeserializer.class)
    private Double salary; // e.g., "50,000"
    // private Double salaryMin;
    // private Double salaryMax;
    private String salaryRange; 
    @JsonDeserialize(using = NullableDoubleDeserializer.class)
    private Double salaryRangeLower;
    @JsonDeserialize(using = NullableDoubleDeserializer.class)
    private Double salaryRangeUpper;
    private String salaryCurrency; // e.g., "USD", "EUR"
    private String salaryFrequency; // e.g., "per year", "per hour", "total"
    private Boolean isNegotiable; // e.g., true if salary is negotiable
    private Boolean toolkitExists;
    @JsonDeserialize(using = NullableIntegerDeserializer.class)
    private Integer serviceDeliveryDays;
    @JsonDeserialize(using = NullableIntegerDeserializer.class)
    private Integer serviceRevisionCount;
    private String serviceIncludes;
    private String portfolioUrl;

    private String requirements;
    private String responsibilities;
    
    private Instant applicationDeadline;
    private Instant postedTime;
    private List<String> imageUrls;

    // Additional fields can be added as needed
    private Boolean isSavedByCurrentUser;

    private Integer applicationCount;
}
