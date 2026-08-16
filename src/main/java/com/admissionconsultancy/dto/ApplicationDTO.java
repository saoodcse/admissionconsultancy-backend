package com.admissionconsultancy.dto;

import com.admissionconsultancy.enums.ApplicationStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ApplicationDTO {
    private Long id;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Applicant email is required")
    private String applicantEmail;

    private ApplicationStatus status;
    private String counselorRemarks;

    @NotNull(message = "University summary is required")
    private UniversitySummaryDTO university;

    @NotNull(message = "Course summary is required")
    private CourseSummaryDTO course;

    private List<String> documentUrls;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}