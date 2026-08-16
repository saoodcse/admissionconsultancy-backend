package com.admissionconsultancy.dto;

import com.admissionconsultancy.enums.LeadStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LeadDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String city;
    private String message;
    private String source;
    private LeadStatus status;
    private String adminNotes;

    // --- Admissions & Intent Tracking ---
    private String targetIntake;             // e.g., "September Intake 2026"
    private Double preferredBudgetUsd;       // e.g., 5000.0 (Annual budget limit)

    // --- Marketing Attribution (UTM Parameters) ---
    private String utmSource;                // e.g., "google", "instagram"
    private String utmMedium;                // e.g., "cpc", "social"
    private String utmCampaign;              // e.g., "mbbs_abroad_summer_sale"

    // --- Assignment & Follow-up ---
    private String assignedCounselorEmail;   // e.g., "counselor@admissionconsultancy.com"

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // --- Output Summaries (Intents) ---
    private CountrySummaryDTO country;
    private UniversitySummaryDTO university;
    private CollegeSummaryDTO college;       // Added college interest tracking
    private CourseSummaryDTO course;
}