package com.admissionconsultancy.service;

import com.admissionconsultancy.dto.UniversityDTO;
import java.util.List;

public interface UniversityService {
    List<UniversityDTO> getActiveUniversities();
    List<UniversityDTO> getAllUniversitiesAdmin();
    List<UniversityDTO> getUniversitiesByCountry(String countrySlug);
    List<UniversityDTO> filterUniversities(String countrySlug, Double maxBudget, Integer maxRanking);
    UniversityDTO getBySlug(String slug);
    UniversityDTO createUniversity(UniversityDTO universityDTO);
    UniversityDTO updateUniversity(String slug, UniversityDTO universityDTO);
    void deleteUniversity(String slug);
}