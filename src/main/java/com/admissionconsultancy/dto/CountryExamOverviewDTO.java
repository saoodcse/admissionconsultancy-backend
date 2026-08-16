package com.admissionconsultancy.dto;

import lombok.Data;
import java.util.List;

@Data
public class CountryExamOverviewDTO {
    private String countryName;
    private String countrySlug;

    // List of Exams explicitly accepted in this country (e.g., ["IELTS", "NEET-UG"])
    private List<ExamDTO> applicableExams;

    // Specific requirements mapped to universities/courses in this country
    private List<RequirementDetailDTO> requirements;
}