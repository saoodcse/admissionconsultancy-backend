package com.admissionconsultancy.controller;

import com.admissionconsultancy.dto.ApiResponse;
import com.admissionconsultancy.dto.ExploreCountryDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/explore")
public interface ExploreControllerApi {

    @GetMapping("/countries")
    ResponseEntity<ApiResponse<List<ExploreCountryDTO>>> getAllExploreCountries();

    @GetMapping("/countries/{slug}")
    ResponseEntity<ApiResponse<ExploreCountryDTO>> getExploreCountryBySlug(@PathVariable("slug") String slug);

    @GetMapping("/search")
    ResponseEntity<ApiResponse<Map<String, Object>>> globalSearch(@RequestParam(name = "q", defaultValue = "") String query);
}