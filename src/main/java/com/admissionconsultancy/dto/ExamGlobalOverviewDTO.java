package com.admissionconsultancy.dto;

import lombok.Data;
import java.util.List;

@Data
public class ExamGlobalOverviewDTO {
    private String examName;
    private String examCode;
    private String description;

    // SEO & Meta Info for Global Overview Page
    private String metaTitle;
    private String metaDescription;

    // List of Country names where this exam is accepted
    private List<String> applicableCountries;

    // Specific requirements across the globe for this exam
    private List<RequirementDetailDTO> globalRequirements;
}