package com.admissionconsultancy.controller;

import com.admissionconsultancy.dto.ApiResponse;
import com.admissionconsultancy.dto.EducationContentDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
public interface EducationContentControllerApi {

    @GetMapping("/content")
    ResponseEntity<ApiResponse<List<EducationContentDTO>>> getActiveContents();

    @GetMapping("/content/category/{category}")
    ResponseEntity<ApiResponse<List<EducationContentDTO>>> getContentsByCategory(@PathVariable("category") String category);

    @GetMapping("/content/country/{countrySlug}")
    ResponseEntity<ApiResponse<List<EducationContentDTO>>> getContentsByCountry(@PathVariable("countrySlug") String countrySlug);

    @GetMapping("/content/university/{universitySlug}")
    ResponseEntity<ApiResponse<List<EducationContentDTO>>> getContentsByUniversity(@PathVariable("universitySlug") String universitySlug);

    @GetMapping("/content/course/{courseSlug}")
    ResponseEntity<ApiResponse<List<EducationContentDTO>>> getContentsByCourse(@PathVariable("courseSlug") String courseSlug);

    @GetMapping("/content/{slug}")
    ResponseEntity<ApiResponse<EducationContentDTO>> getContentBySlug(@PathVariable("slug") String slug);

    @PostMapping("/admin/content")
    ResponseEntity<ApiResponse<EducationContentDTO>> createContent(@Valid @RequestBody EducationContentDTO dto);

    @PutMapping("/admin/content/{slug}")
    ResponseEntity<ApiResponse<EducationContentDTO>> updateContent(@PathVariable("slug") String slug, @Valid @RequestBody EducationContentDTO dto);

    @DeleteMapping("/admin/content/{slug}")
    ResponseEntity<ApiResponse<Void>> deleteContent(@PathVariable("slug") String slug);
}