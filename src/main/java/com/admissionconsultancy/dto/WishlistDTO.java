package com.admissionconsultancy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WishlistDTO {
    private Long id;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Student email is required")
    private String studentEmail;

    private UniversitySummaryDTO university;
    private CollegeSummaryDTO college;
    private CourseSummaryDTO course;

    private LocalDateTime createdAt;
}