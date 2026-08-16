package com.admissionconsultancy.controller.impl;

import com.admissionconsultancy.constants.AppCode;
import com.admissionconsultancy.controller.ExamControllerApi;
import com.admissionconsultancy.dto.*;
import com.admissionconsultancy.service.ExamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ExamController implements ExamControllerApi {

    private final ExamService examService;

    // Public Endpoints

    @Override
    public ResponseEntity<ApiResponse<List<ExamDTO>>> getActiveExams() {
        log.debug("REST request to get active exams");
        List<ExamDTO> exams = examService.getActiveExams();
        return ApiResponse.success(exams, AppCode.SUCCESS);
    }

    @Override
    public ResponseEntity<ApiResponse<ExamDTO>> getExamByCode(@PathVariable("code") String code) {
        log.debug("REST request to get exam by code: {}", code);
        ExamDTO exam = examService.getExamByCode(code);
        return ApiResponse.success(exam, AppCode.SUCCESS);
    }

    @Override
    public ResponseEntity<ApiResponse<CountryExamOverviewDTO>> getExamsByCountrySlug(@PathVariable("countrySlug") String countrySlug) {
        log.debug("REST request: get exam overview for country: {}", countrySlug);
        CountryExamOverviewDTO response = examService.getRequirementsByCountrySlug(countrySlug);
        return ApiResponse.success(response, AppCode.SUCCESS);
    }

    @Override
    public ResponseEntity<ApiResponse<ExamGlobalOverviewDTO>> getGlobalExamOverview(@PathVariable("code") String code) {
        log.debug("REST request: get global overview for exam code: {}", code);
        ExamGlobalOverviewDTO response = examService.getRequirementsByExamCode(code);
        return ApiResponse.success(response, AppCode.SUCCESS);
    }

    // Protected Admin Endpoints

    @Override
    public ResponseEntity<ApiResponse<List<ExamDTO>>> getAllExamsAdmin() {
        log.debug("REST request to get all exams (admin)");
        List<ExamDTO> exams = examService.getAllExamsAdmin();
        return ApiResponse.success(exams, AppCode.SUCCESS);
    }

    @Override
    public ResponseEntity<ApiResponse<ExamDTO>> createExam(@RequestBody ExamDTO examDTO) {
        log.info("REST request to create new exam: {}", examDTO.getCode());
        ExamDTO created = examService.createExam(examDTO);
        return ApiResponse.success(created, AppCode.SUCCESS);
    }

    @Override
    public ResponseEntity<ApiResponse<ExamDTO>> updateExam(@PathVariable("code") String code, @RequestBody ExamDTO examDTO) {
        log.info("REST request to update exam with code: {}", code);
        ExamDTO updated = examService.updateExam(code, examDTO);
        return ApiResponse.success(updated, AppCode.SUCCESS);
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteExam(@PathVariable("code") String code) {
        log.info("REST request to delete exam with code: {}", code);
        examService.deleteExam(code);
        return ApiResponse.success(null, AppCode.SUCCESS);
    }

    // Complex Admin Mapping Endpoints ---

    @Override
    public ResponseEntity<ApiResponse<Void>> mapExamToCountry(@PathVariable("examCode") String examCode, @PathVariable("countrySlug") String countrySlug) {
        log.info("REST request to map Exam Code: {} to Country Slug: {}", examCode, countrySlug);
        examService.mapExamToCountry(examCode, countrySlug);
        return ApiResponse.success(null, AppCode.SUCCESS);
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> addExamRequirement(@RequestBody ExamRequirementDTO reqDTO) {
        log.info("REST request to add exam requirement for Exam Code: {}", reqDTO.getExamCode());
        examService.addExamRequirement(reqDTO);
        return ApiResponse.success(null, AppCode.SUCCESS);
    }
}