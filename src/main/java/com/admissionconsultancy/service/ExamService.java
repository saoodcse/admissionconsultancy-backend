package com.admissionconsultancy.service;

import com.admissionconsultancy.dto.CountryExamOverviewDTO;
import com.admissionconsultancy.dto.ExamDTO;
import com.admissionconsultancy.dto.ExamGlobalOverviewDTO;
import com.admissionconsultancy.dto.ExamRequirementDTO;
import java.util.List;

public interface ExamService {
    List<ExamDTO> getActiveExams();
    List<ExamDTO> getAllExamsAdmin();
    ExamDTO getExamByCode(String code);
    ExamDTO createExam(ExamDTO examDTO);
    ExamDTO updateExam(String code, ExamDTO examDTO);
    void deleteExam(String code);

    void mapExamToCountry(String examCode, String countrySlug);
    void addExamRequirement(ExamRequirementDTO reqDTO);
    CountryExamOverviewDTO getRequirementsByCountrySlug(String countrySlug);
    ExamGlobalOverviewDTO getRequirementsByExamCode(String examCode);
}