package com.admissionconsultancy.controller;

import com.admissionconsultancy.dto.ApiResponse;
import com.admissionconsultancy.dto.UniversityDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
public interface UniversityControllerApi {

    // PUBLIC ENDPOINTS (No Authentication Required)

    @GetMapping("/universities")
    ResponseEntity<ApiResponse<List<UniversityDTO>>> getActiveUniversities();

    @GetMapping("/universities/{slug}")
    ResponseEntity<ApiResponse<UniversityDTO>> getUniversityBySlug(@PathVariable("slug") String slug);

    @GetMapping("/universities/country/{countrySlug}")
    ResponseEntity<ApiResponse<List<UniversityDTO>>> getUniversitiesByCountry(@PathVariable("countrySlug") String countrySlug);

    // ADVANCED FACETED FILTERING ENDPOINT
    @GetMapping("/universities/filter")
    ResponseEntity<ApiResponse<List<UniversityDTO>>> filterUniversities(
            @RequestParam(required = false) String countrySlug,
            @RequestParam(required = false) Double maxBudget,
            @RequestParam(required = false) Integer maxRanking
    );

    // PROTECTED ADMIN ENDPOINTS (Requires JWT Token)

    @GetMapping("/admin/universities/all")
    ResponseEntity<ApiResponse<List<UniversityDTO>>> getAllUniversitiesAdmin();

    @PostMapping("/admin/universities")
    ResponseEntity<ApiResponse<UniversityDTO>> createUniversity(@Valid @RequestBody UniversityDTO universityDTO);

    @PutMapping("/admin/universities/{slug}")
    ResponseEntity<ApiResponse<UniversityDTO>> updateUniversity(
            @PathVariable("slug") String slug,
            @Valid @RequestBody UniversityDTO universityDTO
    );

    @DeleteMapping("/admin/universities/{slug}")
    ResponseEntity<ApiResponse<Void>> deleteUniversity(@PathVariable("slug") String slug);
}