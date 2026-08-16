package com.admissionconsultancy.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExamDTO {

    @NotBlank(message = "Exam name is required")
    private String name;

    @NotBlank(message = "Exam code is required")
    private String code;

    private String description;

    // SEO & OpenGraph Meta Tags
    private String metaTitle;
    private String metaDescription;
    private String keywords;
    private String ogImage;

    // Testing & Administration Details
    private String conductingBody;      // e.g., "NTA", "British Council", "ETS"
    private String examMode;            // e.g., "Computer-Based / Online"
    private String scoreValidity;       // e.g., "2 Years"
    private String officialWebsiteUrl;  // e.g., "https://www.ets.org/toefl"

    private boolean isActive;
}