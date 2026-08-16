package com.admissionconsultancy.controller.impl;

import com.admissionconsultancy.constants.AppCode;
import com.admissionconsultancy.controller.EducationContentControllerApi;
import com.admissionconsultancy.dto.ApiResponse;
import com.admissionconsultancy.dto.EducationContentDTO;
import com.admissionconsultancy.service.EducationContentService;
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
public class EducationContentController implements EducationContentControllerApi {

    private final EducationContentService contentService;

    @Override
    public ResponseEntity<ApiResponse<List<EducationContentDTO>>> getActiveContents() {
        log.debug("REST request: get all active education contents");
        return ApiResponse.success(contentService.getActiveContents(), AppCode.SUCCESS);
    }

    @Override
    public ResponseEntity<ApiResponse<List<EducationContentDTO>>> getContentsByCategory(@PathVariable("category") String category) {
        log.debug("REST request: get contents by category '{}'", category);
        return ApiResponse.success(contentService.getContentsByCategory(category), AppCode.SUCCESS);
    }

    @Override
    public ResponseEntity<ApiResponse<List<EducationContentDTO>>> getContentsByCountry(@PathVariable("countrySlug") String countrySlug) {
        log.debug("REST request: get contents by country slug '{}'", countrySlug);
        return ApiResponse.success(contentService.getContentsByCountry(countrySlug), AppCode.SUCCESS);
    }

    @Override
    public ResponseEntity<ApiResponse<List<EducationContentDTO>>> getContentsByUniversity(@PathVariable("universitySlug") String universitySlug) {
        log.debug("REST request: get contents by university slug '{}'", universitySlug);
        return ApiResponse.success(contentService.getContentsByUniversity(universitySlug), AppCode.SUCCESS);
    }

    @Override
    public ResponseEntity<ApiResponse<List<EducationContentDTO>>> getContentsByCourse(@PathVariable("courseSlug") String courseSlug) {
        log.debug("REST request: get contents by course slug '{}'", courseSlug);
        return ApiResponse.success(contentService.getContentsByCourse(courseSlug), AppCode.SUCCESS);
    }

    @Override
    public ResponseEntity<ApiResponse<EducationContentDTO>> getContentBySlug(@PathVariable("slug") String slug) {
        log.debug("REST request: get content by slug '{}'", slug);
        return ApiResponse.success(contentService.getContentBySlug(slug), AppCode.SUCCESS);
    }

    @Override
    public ResponseEntity<ApiResponse<EducationContentDTO>> createContent(@RequestBody EducationContentDTO dto) {
        log.debug("REST request: create content with slug '{}'", dto.getUrlSlug());
        return ApiResponse.success(contentService.createContent(dto), AppCode.SUCCESS);
    }

    @Override
    public ResponseEntity<ApiResponse<EducationContentDTO>> updateContent(@PathVariable("slug") String slug, @RequestBody EducationContentDTO dto) {
        log.debug("REST request: update content with slug '{}'", slug);
        return ApiResponse.success(contentService.updateContent(slug, dto), AppCode.SUCCESS);
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteContent(@PathVariable("slug") String slug) {
        log.debug("REST request: delete content with slug '{}'", slug);
        contentService.deleteContent(slug);
        return ApiResponse.success(null, AppCode.SUCCESS);
    }
}