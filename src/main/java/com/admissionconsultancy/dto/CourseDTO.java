package com.admissionconsultancy.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CourseDTO {

    @NotBlank(message = "Course name cannot be blank")
    private String name;

    @NotBlank(message = "Slug cannot be blank")
    private String slug;

    private String fullName;
    private String description;
    private String duration;
    private String degreeType;
    private String studyLevel;
    private String field;
    private String eligibility;

    // Pricing & Currency (Native + USD Normalized)
    private Double minTuitionFee;
    private Double maxTuitionFee;
    private String feeCurrency;
    private String tuitionFeeNative;
    private Double minTuitionUsd;
    private Double maxTuitionUsd;

    // SEO & OpenGraph Meta Tags
    private String metaTitle;
    private String metaDescription;
    private String keywords;
    private String ogImage;

    // Admissions, Intakes & Exams
    private List<String> availableIntakes;    // e.g., ["September Intake", "January Intake"]
    private String applicationDeadline;       // e.g., "August 31st"
    private List<String> requiredExams;       // e.g., ["NEET", "IELTS"]

    private String image;

    private boolean isActive;
    private Integer popularityScore;
    private Integer viewCount;
    private Double rating;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Primitive Collections
    private List<String> subjects;
    private List<String> careerProspects;

    // Output field (Universities offering this course)
    private List<UniversitySummaryDTO> universities;
}