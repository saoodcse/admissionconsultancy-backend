package com.admissionconsultancy.dto;

import lombok.Data;
import java.util.List;

@Data
public class ExploreCountryDTO {
    private CountryDTO country;
    private List<UniversitySummaryDTO> universities;
    private List<CourseSummaryDTO> courses;
    private List<CollegeSummaryDTO> colleges;
}