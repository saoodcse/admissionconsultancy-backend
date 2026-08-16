package com.admissionconsultancy.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UniversityDTO {

    @NotBlank(message = "University name cannot be blank")
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
    private String tuitionFeeNative;          // e.g., "400,000 RUB / Year"
    private Double minTuitionUsd;             // Normalized for global sorting/filtering
    private Double maxTuitionUsd;

    // SEO & OpenGraph Meta Tags
    private String metaTitle;
    private String metaDescription;
    private String keywords;
    private String ogImage;

    // Admissions & Student Life
    private List<String> availableIntakes;    // e.g., ["September Intake"]
    private String applicationDeadline;       // e.g., "August 31st"
    private String baselineEligibility;       // e.g., "50% in PCB + NEET Qualified"
    private Boolean hasOnCampusHostel;        // true/false
    private Boolean hasInternationalMess;     // true/false
    private Double estimatedMonthlyLivingUsd; // e.g., 250.0

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

    // Output fields (For GET requests - returning data to the frontend)
    private CountrySummaryDTO country;
    private List<CollegeSummaryDTO> colleges;
    private List<CourseSummaryDTO> offeredCourses;
}