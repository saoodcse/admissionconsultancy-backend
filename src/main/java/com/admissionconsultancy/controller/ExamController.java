package com.admissionconsultancy.controller;

import com.admissionconsultancy.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
public interface ExamController {

    // Public Endpoints

    @GetMapping("/exams")
    ResponseEntity<ApiResponse<List<ExamDTO>>> getActiveExams();

    @GetMapping("/exams/{code}")
    ResponseEntity<ApiResponse<ExamDTO>> getExamByCode(@PathVariable("code") String code);

    @GetMapping("/countries/{countrySlug}/exams")
    ResponseEntity<ApiResponse<CountryExamOverviewDTO>> getExamsByCountrySlug(@PathVariable("countrySlug") String countrySlug);

    @GetMapping("/exams/{code}/overview")
    ResponseEntity<ApiResponse<ExamGlobalOverviewDTO>> getGlobalExamOverview(@PathVariable("code") String code);

    // Protected Admin Endpoints

    @GetMapping("/admin/exams")
    ResponseEntity<ApiResponse<List<ExamDTO>>> getAllExamsAdmin();

    @PostMapping("/admin/exams")
    ResponseEntity<ApiResponse<ExamDTO>> createExam(@Valid @RequestBody ExamDTO examDTO);

    @PutMapping("/admin/exams/{code}")
    ResponseEntity<ApiResponse<ExamDTO>> updateExam(
            @PathVariable("code") String code,
            @Valid @RequestBody ExamDTO examDTO
    );

    @DeleteMapping("/admin/exams/{code}")
    ResponseEntity<ApiResponse<Void>> deleteExam(@PathVariable("code") String code);

    // Complex Admin Mapping Endpoints

    @PostMapping("/admin/exams/{examCode}/countries/{countrySlug}")
    ResponseEntity<ApiResponse<Void>> mapExamToCountry(
            @PathVariable("examCode") String examCode,
            @PathVariable("countrySlug") String countrySlug
    );

    @PostMapping("/admin/exams/requirements")
    ResponseEntity<ApiResponse<Void>> addExamRequirement(@Valid @RequestBody ExamRequirementDTO reqDTO);
}