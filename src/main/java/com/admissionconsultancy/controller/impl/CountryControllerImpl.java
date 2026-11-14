package com.admissionconsultancy.controller.impl;

import com.admissionconsultancy.constants.AppCode;
import com.admissionconsultancy.controller.CountryController; // Imported from the parent package
import com.admissionconsultancy.dto.ApiResponse;
import com.admissionconsultancy.dto.CountryDTO;
import com.admissionconsultancy.service.CountryService;
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
public class CountryControllerImpl implements CountryController {

    private final CountryService countryService;

    // PUBLIC ENDPOINTS (No Authentication Required)

    @Override
    public ResponseEntity<ApiResponse<List<CountryDTO>>> getActiveCountries() {
        log.debug("REST request to fetch all active countries");
        List<CountryDTO> countries = countryService.getActiveCountries();
        return ApiResponse.success(countries, AppCode.COUNTRY_FETCHED);
    }

    @Override
    public ResponseEntity<ApiResponse<CountryDTO>> getCountryBySlug(@PathVariable("slug") String slug) {
        log.debug("REST request to fetch country details for slug: '{}'", slug);
        CountryDTO country = countryService.getCountryBySlug(slug);
        return ApiResponse.success(country, AppCode.COUNTRY_FETCHED);
    }

    // PROTECTED ADMIN ENDPOINTS (Requires JWT Token)

    @Override
    public ResponseEntity<ApiResponse<List<CountryDTO>>> getAllCountriesAdmin() {
        log.debug("REST request to fetch all countries (Admin View)");
        List<CountryDTO> countries = countryService.getAllCountriesAdmin();
        return ApiResponse.success(countries, AppCode.COUNTRY_FETCHED);
    }

    @Override
    public ResponseEntity<ApiResponse<CountryDTO>> createCountry(@RequestBody CountryDTO countryDTO) {
        log.debug("REST request to create new country: '{}'", countryDTO.getName());
        CountryDTO createdCountry = countryService.createCountry(countryDTO);
        return ApiResponse.success(createdCountry, AppCode.COUNTRY_CREATED);
    }

    @Override
    public ResponseEntity<ApiResponse<CountryDTO>> updateCountry(@PathVariable("slug") String slug, @RequestBody CountryDTO countryDTO) {
        log.debug("REST request to update country with slug: {}", slug);
        CountryDTO updatedCountry = countryService.updateCountry(slug, countryDTO);
        return ApiResponse.success(updatedCountry, AppCode.COUNTRY_UPDATED);
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteCountry(@PathVariable("slug") String slug) {
        log.debug("REST request to soft-delete country with slug: {}", slug);
        countryService.deleteCountry(slug);
        return ApiResponse.success(null, AppCode.COUNTRY_DELETED);
    }
}