package com.admissionconsultancy.service;

import com.admissionconsultancy.dto.CollegeDTO;
import java.util.List;

public interface CollegeService {
    List<CollegeDTO> getActiveColleges();
    List<CollegeDTO> getAllCollegesAdmin();
    List<CollegeDTO> getCollegesByCountry(String countrySlug);
    CollegeDTO getBySlug(String slug);
    CollegeDTO createCollege(CollegeDTO collegeDTO);
    CollegeDTO updateCollege(String slug, CollegeDTO collegeDTO);
    void deleteCollege(String slug);
}