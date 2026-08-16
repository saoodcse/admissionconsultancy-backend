package com.admissionconsultancy.service;

import com.admissionconsultancy.dto.ExploreCountryDTO;
import java.util.List;
import java.util.Map;

public interface ExploreService {
    List<ExploreCountryDTO> getAllExploreCountries();
    ExploreCountryDTO getExploreCountryBySlug(String slug);
    Map<String, Object> globalSearch(String query);
}
