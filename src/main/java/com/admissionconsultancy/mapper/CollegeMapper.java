package com.admissionconsultancy.mapper;

import com.admissionconsultancy.dto.CollegeDTO;
import com.admissionconsultancy.dto.CountrySummaryDTO;
import com.admissionconsultancy.dto.CourseSummaryDTO;
import com.admissionconsultancy.dto.UniversitySummaryDTO;
import com.admissionconsultancy.entity.College;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class CollegeMapper {

    public CollegeDTO toDTO(College entity) {
        if (entity == null) return null;

        CollegeDTO dto = new CollegeDTO();
        dto.setName(entity.getName());
        dto.setSlug(entity.getSlug());
        dto.setDescription(entity.getDescription());
        dto.setEstablished(entity.getEstablished());
        dto.setType(entity.getType());
        dto.setRanking(entity.getRanking());

        // Pricing & Currency
        dto.setMinTuitionFee(entity.getMinTuitionFee());
        dto.setMaxTuitionFee(entity.getMaxTuitionFee());
        dto.setFeeCurrency(entity.getFeeCurrency());
        dto.setTuitionFeeNative(entity.getTuitionFeeNative());
        dto.setMinTuitionUsd(entity.getMinTuitionUsd());
        dto.setMaxTuitionUsd(entity.getMaxTuitionUsd());

        // SEO & OpenGraph Meta Tags
        dto.setMetaTitle(entity.getMetaTitle());
        dto.setMetaDescription(entity.getMetaDescription());
        dto.setKeywords(entity.getKeywords());
        dto.setOgImage(entity.getOgImage());

        // Admissions & Student Life
        if (entity.getAvailableIntakes() != null) {
            dto.setAvailableIntakes(new ArrayList<>(entity.getAvailableIntakes()));
        }
        dto.setApplicationDeadline(entity.getApplicationDeadline());
        dto.setBaselineEligibility(entity.getBaselineEligibility());
        dto.setHasOnCampusHostel(entity.getHasOnCampusHostel());
        dto.setHasInternationalMess(entity.getHasInternationalMess());
        dto.setEstimatedMonthlyLivingUsd(entity.getEstimatedMonthlyLivingUsd());

        dto.setImage(entity.getImage());
        dto.setLogo(entity.getLogo());
        dto.setActive(entity.isActive());
        dto.setPopularityScore(entity.getPopularityScore());
        dto.setViewCount(entity.getViewCount());
        dto.setRating(entity.getRating());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getAccreditation() != null) dto.setAccreditation(new ArrayList<>(entity.getAccreditation()));
        if (entity.getFacilities() != null) dto.setFacilities(new ArrayList<>(entity.getFacilities()));

        // Map Country Summary
        if (entity.getCountry() != null) {
            CountrySummaryDTO countrySummary = new CountrySummaryDTO();
            countrySummary.setName(entity.getCountry().getName());
            countrySummary.setSlug(entity.getCountry().getSlug());
            countrySummary.setFlag(entity.getCountry().getFlag());
            dto.setCountry(countrySummary);
        }

        // Map University Summary
        if (entity.getUniversity() != null) {
            UniversitySummaryDTO uniSummary = new UniversitySummaryDTO();
            uniSummary.setName(entity.getUniversity().getName());
            uniSummary.setSlug(entity.getUniversity().getSlug());
            uniSummary.setLogo(entity.getUniversity().getLogo());
            if (entity.getUniversity().getCountry() != null) {
                uniSummary.setCountryName(entity.getUniversity().getCountry().getName());
            }
            dto.setUniversity(uniSummary);
        }

        // Map Offered Courses Summary List
        if (entity.getOfferedCourses() != null) {
            dto.setOfferedCourses(entity.getOfferedCourses().stream().map(course -> {
                CourseSummaryDTO courseSummary = new CourseSummaryDTO();
                courseSummary.setName(course.getName());
                courseSummary.setSlug(course.getSlug());
                courseSummary.setDegreeType(course.getDegreeType());
                return courseSummary;
            }).collect(Collectors.toList()));
        }

        return dto;
    }

    public void updateEntityFromDTO(CollegeDTO dto, College entity) {
        if (dto == null || entity == null) return;

        entity.setName(dto.getName());
        entity.setSlug(dto.getSlug());
        entity.setDescription(dto.getDescription());
        entity.setEstablished(dto.getEstablished());
        entity.setType(dto.getType());
        entity.setRanking(dto.getRanking());

        // Pricing & Currency
        entity.setMinTuitionFee(dto.getMinTuitionFee());
        entity.setMaxTuitionFee(dto.getMaxTuitionFee());
        entity.setFeeCurrency(dto.getFeeCurrency());
        entity.setTuitionFeeNative(dto.getTuitionFeeNative());
        entity.setMinTuitionUsd(dto.getMinTuitionUsd());
        entity.setMaxTuitionUsd(dto.getMaxTuitionUsd());

        // SEO & OpenGraph Meta Tags
        entity.setMetaTitle(dto.getMetaTitle());
        entity.setMetaDescription(dto.getMetaDescription());
        entity.setKeywords(dto.getKeywords());
        entity.setOgImage(dto.getOgImage());

        // Admissions & Student Life
        entity.setApplicationDeadline(dto.getApplicationDeadline());
        entity.setBaselineEligibility(dto.getBaselineEligibility());
        entity.setHasOnCampusHostel(dto.getHasOnCampusHostel());
        entity.setHasInternationalMess(dto.getHasInternationalMess());
        entity.setEstimatedMonthlyLivingUsd(dto.getEstimatedMonthlyLivingUsd());

        entity.setImage(dto.getImage());
        entity.setLogo(dto.getLogo());
        entity.setActive(dto.isActive());

        if (dto.getPopularityScore() != null) entity.setPopularityScore(dto.getPopularityScore());
        if (dto.getViewCount() != null) entity.setViewCount(dto.getViewCount());
        if (dto.getRating() != null) entity.setRating(dto.getRating());

        entity.getAvailableIntakes().clear();
        if (dto.getAvailableIntakes() != null) entity.getAvailableIntakes().addAll(dto.getAvailableIntakes());

        entity.getAccreditation().clear();
        if (dto.getAccreditation() != null) entity.getAccreditation().addAll(dto.getAccreditation());

        entity.getFacilities().clear();
        if (dto.getFacilities() != null) entity.getFacilities().addAll(dto.getFacilities());
    }
}