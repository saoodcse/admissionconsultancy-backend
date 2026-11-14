package com.admissionconsultancy.controller.impl;

import com.admissionconsultancy.constants.AppCode;
import com.admissionconsultancy.controller.ExploreController;
import com.admissionconsultancy.dto.ApiResponse;
import com.admissionconsultancy.dto.ExploreCountryDTO;
import com.admissionconsultancy.service.ExploreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ExploreControllerImpl implements ExploreController {

    private final ExploreService exploreService;

    @Override
    public ResponseEntity<ApiResponse<List<ExploreCountryDTO>>> getAllExploreCountries() {
        log.debug("REST request: get all explore countries");
        List<ExploreCountryDTO> exploreList = exploreService.getAllExploreCountries();
        return ApiResponse.success(exploreList, AppCode.SUCCESS);
    }

    @Override
    public ResponseEntity<ApiResponse<ExploreCountryDTO>> getExploreCountryBySlug(@PathVariable("slug") String slug) {
        log.debug("REST request: get explore country data for slug '{}'", slug);
        ExploreCountryDTO exploreData = exploreService.getExploreCountryBySlug(slug);
        return ApiResponse.success(exploreData, AppCode.SUCCESS);
    }

    @Override
    public ResponseEntity<ApiResponse<Map<String, Object>>> globalSearch(@RequestParam(name = "q", defaultValue = "") String query) {
        log.debug("REST request: global search for query '{}'", query);
        Map<String, Object> results = exploreService.globalSearch(query);
        return ApiResponse.success(results, AppCode.SUCCESS);
    }
}