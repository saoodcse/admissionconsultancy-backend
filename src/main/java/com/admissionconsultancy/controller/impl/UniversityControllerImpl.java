package com.admissionconsultancy.controller.impl;

import com.admissionconsultancy.constants.AppCode;
import com.admissionconsultancy.controller.UniversityController;
import com.admissionconsultancy.dto.ApiResponse;
import com.admissionconsultancy.dto.UniversityDTO;
import com.admissionconsultancy.service.UniversityService;
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
public class UniversityControllerImpl implements UniversityController {

    private final UniversityService universityService;

    // PUBLIC ENDPOINTS (No Authentication Required)

    @Override
    public ResponseEntity<ApiResponse<List<UniversityDTO>>> getActiveUniversities() {
        log.debug("REST request: get all active universities");
        List<UniversityDTO> universities = universityService.getActiveUniversities();
        return ApiResponse.success(universities, AppCode.UNIVERSITY_FETCHED);
    }

    @Override
    public ResponseEntity<ApiResponse<UniversityDTO>> getUniversityBySlug(@PathVariable("slug") String slug) {
        log.debug("REST request: get university by slug '{}'", slug);
        UniversityDTO university = universityService.getBySlug(slug);
        return ApiResponse.success(university, AppCode.UNIVERSITY_FETCHED);
    }

    @Override
    public ResponseEntity<ApiResponse<List<UniversityDTO>>> getUniversitiesByCountry(@PathVariable("countrySlug") String countrySlug) {
        log.debug("REST request: get universities for country slug '{}'", countrySlug);
        List<UniversityDTO> universities = universityService.getUniversitiesByCountry(countrySlug);
        return ApiResponse.success(universities, AppCode.UNIVERSITY_FETCHED);
    }

    @Override
    public ResponseEntity<ApiResponse<List<UniversityDTO>>> filterUniversities(
            String countrySlug,
            Double maxBudget,
            Integer maxRanking) {
        log.debug("REST request: filter universities -> country: {}, maxBudget: {}, maxRanking: {}", countrySlug, maxBudget, maxRanking);
        List<UniversityDTO> universities = universityService.filterUniversities(countrySlug, maxBudget, maxRanking);
        return ApiResponse.success(universities, AppCode.UNIVERSITY_FETCHED);
    }

    // PROTECTED ADMIN ENDPOINTS (Requires JWT Token)

    @Override
    public ResponseEntity<ApiResponse<List<UniversityDTO>>> getAllUniversitiesAdmin() {
        log.debug("REST request: get all universities (Admin View)");
        List<UniversityDTO> universities = universityService.getAllUniversitiesAdmin();
        return ApiResponse.success(universities, AppCode.UNIVERSITY_FETCHED);
    }

    @Override
    public ResponseEntity<ApiResponse<UniversityDTO>> createUniversity(@RequestBody UniversityDTO universityDTO) {
        log.debug("REST request: create new university: '{}'", universityDTO.getName());
        UniversityDTO createdUniversity = universityService.createUniversity(universityDTO);
        return ApiResponse.success(createdUniversity, AppCode.UNIVERSITY_CREATED);
    }

    @Override
    public ResponseEntity<ApiResponse<UniversityDTO>> updateUniversity(
            @PathVariable("slug") String slug,
            @RequestBody UniversityDTO universityDTO) {

        log.debug("REST request: update university with slug: {}", slug);
        UniversityDTO updatedUniversity = universityService.updateUniversity(slug, universityDTO);
        return ApiResponse.success(updatedUniversity, AppCode.UNIVERSITY_UPDATED);
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteUniversity(@PathVariable("slug") String slug) {
        log.debug("REST request: soft-delete university with slug: {}", slug);
        universityService.deleteUniversity(slug);
        return ApiResponse.success(null, AppCode.UNIVERSITY_DELETED);
    }


}