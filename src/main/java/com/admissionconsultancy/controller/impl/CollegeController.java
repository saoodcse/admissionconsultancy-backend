package com.admissionconsultancy.controller.impl;

import com.admissionconsultancy.constants.AppCode;
import com.admissionconsultancy.controller.CollegeControllerApi;
import com.admissionconsultancy.dto.ApiResponse;
import com.admissionconsultancy.dto.CollegeDTO;
import com.admissionconsultancy.service.CollegeService;
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
public class CollegeController implements CollegeControllerApi {

    private final CollegeService collegeService;

    // Public Endpoints
    @Override
    public ResponseEntity<ApiResponse<List<CollegeDTO>>> getActiveColleges() {
        log.debug("REST request: get all active colleges");
        List<CollegeDTO> colleges = collegeService.getActiveColleges();
        return ApiResponse.success(colleges, AppCode.SUCCESS);
    }

    @Override
    public ResponseEntity<ApiResponse<CollegeDTO>> getCollegeBySlug(@PathVariable("slug") String slug) {
        log.debug("REST request: get college by slug '{}'", slug);
        CollegeDTO college = collegeService.getBySlug(slug);
        return ApiResponse.success(college, AppCode.SUCCESS);
    }

    @Override
    public ResponseEntity<ApiResponse<List<CollegeDTO>>> getCollegesByCountry(@PathVariable("countrySlug") String countrySlug) {
        log.debug("REST request: get colleges for country slug '{}'", countrySlug);
        List<CollegeDTO> colleges = collegeService.getCollegesByCountry(countrySlug);
        return ApiResponse.success(colleges, AppCode.SUCCESS);
    }

    // Protected Admin Endpoints
    @Override
    public ResponseEntity<ApiResponse<List<CollegeDTO>>> getAllCollegesAdmin() {
        log.debug("REST request: get all colleges (Admin View)");
        List<CollegeDTO> colleges = collegeService.getAllCollegesAdmin();
        return ApiResponse.success(colleges, AppCode.SUCCESS);
    }

    @Override
    public ResponseEntity<ApiResponse<CollegeDTO>> createCollege(@RequestBody CollegeDTO collegeDTO) {
        log.debug("REST request: create new college: '{}'", collegeDTO.getName());
        CollegeDTO created = collegeService.createCollege(collegeDTO);
        return ApiResponse.success(created, AppCode.SUCCESS);
    }

    @Override
    public ResponseEntity<ApiResponse<CollegeDTO>> updateCollege(@PathVariable("slug") String slug, @RequestBody CollegeDTO collegeDTO) {
        log.debug("REST request: update college with slug: {}", slug);
        CollegeDTO updated = collegeService.updateCollege(slug, collegeDTO);
        return ApiResponse.success(updated, AppCode.SUCCESS);
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteCollege(@PathVariable("slug") String slug) {
        log.debug("REST request: soft-delete college with slug: {}", slug);
        collegeService.deleteCollege(slug);
        return ApiResponse.success(null, AppCode.SUCCESS);
    }
}