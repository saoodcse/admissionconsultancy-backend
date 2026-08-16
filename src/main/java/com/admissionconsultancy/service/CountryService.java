package com.admissionconsultancy.service;

import com.admissionconsultancy.dto.CountryDTO;
import java.util.List;

public interface CountryService {
    List<CountryDTO> getActiveCountries();
    List<CountryDTO> getAllCountriesAdmin();
    CountryDTO getCountryBySlug(String slug);
    CountryDTO createCountry(CountryDTO countryDTO);
    CountryDTO updateCountry(String slug, CountryDTO countryDTO);
    void deleteCountry(String slug);
}