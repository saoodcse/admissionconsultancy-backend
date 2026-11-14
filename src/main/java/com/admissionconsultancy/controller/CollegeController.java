package com.admissionconsultancy.controller;

import com.admissionconsultancy.dto.ApiResponse;
import com.admissionconsultancy.dto.CollegeDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
public interface CollegeController {

    // Public Endpoints

    @GetMapping("/colleges")
    ResponseEntity<ApiResponse<List<CollegeDTO>>> getActiveColleges();

    @GetMapping("/colleges/{slug}")
    ResponseEntity<ApiResponse<CollegeDTO>> getCollegeBySlug(@PathVariable("slug") String slug);

    @GetMapping("/countries/{countrySlug}/colleges")
    ResponseEntity<ApiResponse<List<CollegeDTO>>> getCollegesByCountry(@PathVariable("countrySlug") String countrySlug);

    // Protected Admin Endpoints

    @GetMapping("/admin/colleges")
    ResponseEntity<ApiResponse<List<CollegeDTO>>> getAllCollegesAdmin();

    @PostMapping("/admin/colleges")
    ResponseEntity<ApiResponse<CollegeDTO>> createCollege(@Valid @RequestBody CollegeDTO collegeDTO);

    @PutMapping("/admin/colleges/{slug}")
    ResponseEntity<ApiResponse<CollegeDTO>> updateCollege(
            @PathVariable("slug") String slug,
            @Valid @RequestBody CollegeDTO collegeDTO
    );

    @DeleteMapping("/admin/colleges/{slug}")
    ResponseEntity<ApiResponse<Void>> deleteCollege(@PathVariable("slug") String slug);
}