package com.admissionconsultancy.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EducationContentDTO {
    private Long id;

    @NotBlank(message = "Content type is required")
    private String contentType;

    @NotBlank(message = "SEO title is required")
    private String seoTitle;

    @NotBlank(message = "Meta title is required")
    private String metaTitle;

    @NotBlank(message = "Meta description is required")
    private String metaDescription;

    @NotBlank(message = "URL slug is required")
    private String urlSlug;

    @NotBlank(message = "Primary keyword is required")
    private String primaryKeyword;

    private String secondaryKeywords;

    @NotBlank(message = "Article content is required")
    private String articleContent;

    private String faqs;
    private String category;
    private boolean active;

    // --- Tagging Summaries ---
    private CountrySummaryDTO country;
    private UniversitySummaryDTO university;
    private CourseSummaryDTO course;
}