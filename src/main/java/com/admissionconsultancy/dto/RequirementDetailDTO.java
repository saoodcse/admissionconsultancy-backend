package com.admissionconsultancy.dto;

import lombok.Data;

@Data
public class RequirementDetailDTO {
    private Long requirementId;

    private String examName;
    private String examCode;

    private String countryName;

    // Will be null if the requirement applies broadly
    private String universityName;
    private String collegeName;
    private String courseName;

    private boolean required;
    private Double minimumScore;
    private Double minimumPercentile;
}