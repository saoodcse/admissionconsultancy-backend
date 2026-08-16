package com.admissionconsultancy.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExamRequirementDTO {

    @NotBlank(message = "Exam code is required")
    private String examCode;

    @NotBlank(message = "Country slug is required")
    private String countrySlug;

    private String universitySlug;
    private String collegeSlug;
    private String courseSlug;

    private boolean required;
    private Double minimumScore;
    private Double minimumPercentile;
}