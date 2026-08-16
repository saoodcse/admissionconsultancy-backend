package com.admissionconsultancy.service;

import com.admissionconsultancy.dto.EducationContentDTO;
import java.util.List;

public interface EducationContentService {
    List<EducationContentDTO> getActiveContents();
    List<EducationContentDTO> getContentsByCategory(String category);
    List<EducationContentDTO> getContentsByCountry(String countrySlug);
    List<EducationContentDTO> getContentsByUniversity(String universitySlug);
    List<EducationContentDTO> getContentsByCourse(String courseSlug);
    EducationContentDTO getContentBySlug(String slug);
    EducationContentDTO createContent(EducationContentDTO contentDTO);
    EducationContentDTO updateContent(String slug, EducationContentDTO contentDTO);
    void deleteContent(String slug);
}