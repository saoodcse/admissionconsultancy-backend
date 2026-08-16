package com.admissionconsultancy.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CollegeDTO {

    @NotBlank(message = "College name cannot be blank")
    private String name;

    @NotBlank(message = "Slug cannot be blank")
    private String slug;

    private String description;
    private Integer established;
    private String type;
    private Integer ranking;

    // Pricing & Currency
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

    // Admissions & Student Life
    private List<String> availableIntakes;
    private String applicationDeadline;
    private String baselineEligibility;
    private Boolean hasOnCampusHostel;
    private Boolean hasInternationalMess;
    private Double estimatedMonthlyLivingUsd;

    private String image;
    private String logo;

    private boolean isActive;
    private Integer popularityScore;
    private Integer viewCount;
    private Double rating;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<String> accreditation;
    private List<String> facilities;

    // Output Summary DTOs for GET requests
    private CountrySummaryDTO country;
    private UniversitySummaryDTO university;
    private List<CourseSummaryDTO> offeredCourses;
}