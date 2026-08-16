package com.admissionconsultancy.controller;

import com.admissionconsultancy.dto.ApiResponse;
import com.admissionconsultancy.dto.CountryDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
public interface CountryControllerApi {

    @GetMapping("/countries")
    ResponseEntity<ApiResponse<List<CountryDTO>>> getActiveCountries();

    @GetMapping("/countries/{slug}")
    ResponseEntity<ApiResponse<CountryDTO>> getCountryBySlug(@PathVariable("slug") String slug);

    @GetMapping("/admin/countries/all")
    ResponseEntity<ApiResponse<List<CountryDTO>>> getAllCountriesAdmin();

    @PostMapping("/admin/countries")
    ResponseEntity<ApiResponse<CountryDTO>> createCountry(@Valid @RequestBody CountryDTO countryDTO);

    @PutMapping("/admin/countries/{slug}")
    ResponseEntity<ApiResponse<CountryDTO>> updateCountry(@PathVariable("slug") String slug, @Valid @RequestBody CountryDTO countryDTO);

    @DeleteMapping("/admin/countries/{slug}")
    ResponseEntity<ApiResponse<Void>> deleteCountry(@PathVariable("slug") String slug);
}