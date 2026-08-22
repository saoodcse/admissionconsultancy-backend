package com.admissionconsultancy.mapper;

import com.admissionconsultancy.dto.CollegeSummaryDTO;
import com.admissionconsultancy.dto.CourseDTO;
import com.admissionconsultancy.dto.UniversitySummaryDTO;
import com.admissionconsultancy.entity.Course;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class CourseMapper {

    public CourseDTO toDTO(Course entity) {
        if (entity == null) return null;

        CourseDTO dto = new CourseDTO();
        dto.setName(entity.getName());
        dto.setSlug(entity.getSlug());
        dto.setFullName(entity.getFullName());
        dto.setDescription(entity.getDescription());
        dto.setDuration(entity.getDuration());
        dto.setDegreeType(entity.getDegreeType());
        dto.setStudyLevel(entity.getStudyLevel());
        dto.setField(entity.getField());
        dto.setEligibility(entity.getEligibility());

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

        // Admissions, Intakes & Exams
        if (entity.getAvailableIntakes() != null) {
            dto.setAvailableIntakes(new ArrayList<>(entity.getAvailableIntakes()));
        }
        dto.setApplicationDeadline(entity.getApplicationDeadline());
        if (entity.getRequiredExams() != null) {
            dto.setRequiredExams(new ArrayList<>(entity.getRequiredExams()));
        }

        dto.setImage(entity.getImage());

        dto.setActive(entity.isActive());
        dto.setPopularityScore(entity.getPopularityScore());
        dto.setViewCount(entity.getViewCount());
        dto.setRating(entity.getRating());

        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getSubjects() != null) dto.setSubjects(new ArrayList<>(entity.getSubjects()));
        if (entity.getCareerProspects() != null) dto.setCareerProspects(new ArrayList<>(entity.getCareerProspects()));

        // Map Universities to UniversitySummaryDTO list
        if (entity.getUniversities() != null) {
            dto.setUniversities(entity.getUniversities().stream().map(uni -> {
                UniversitySummaryDTO summary = new UniversitySummaryDTO();
                summary.setName(uni.getName());
                summary.setSlug(uni.getSlug());
                summary.setLogo(uni.getLogo());
                if (uni.getCountry() != null) {
                    summary.setCountryName(uni.getCountry().getName());
                }
                return summary;
            }).collect(Collectors.toList()));
        }

        // Map Colleges to CollegeSummaryDTO list
        if (entity.getColleges() != null) {
            dto.setColleges(entity.getColleges().stream().map(college -> {
                CollegeSummaryDTO summary = new CollegeSummaryDTO();
                summary.setName(college.getName());
                summary.setSlug(college.getSlug());
                summary.setLogo(college.getLogo());
                if (college.getCountry() != null) {
                    summary.setCountryName(college.getCountry().getName());
                }
                return summary;
            }).collect(Collectors.toList()));
        }

        return dto;
    }

    public void updateEntityFromDTO(CourseDTO dto, Course entity) {
        if (dto == null || entity == null) return;

        entity.setName(dto.getName());
        entity.setSlug(dto.getSlug());
        entity.setFullName(dto.getFullName());
        entity.setDescription(dto.getDescription());
        entity.setDuration(dto.getDuration());
        entity.setDegreeType(dto.getDegreeType());
        entity.setStudyLevel(dto.getStudyLevel());
        entity.setField(dto.getField());
        entity.setEligibility(dto.getEligibility());

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

        // Admissions, Intakes & Exams
        entity.setApplicationDeadline(dto.getApplicationDeadline());

        entity.setImage(dto.getImage());
        entity.setActive(dto.isActive());

        if (dto.getPopularityScore() != null) entity.setPopularityScore(dto.getPopularityScore());
        if (dto.getViewCount() != null) entity.setViewCount(dto.getViewCount());
        if (dto.getRating() != null) entity.setRating(dto.getRating());

        entity.getAvailableIntakes().clear();
        if (dto.getAvailableIntakes() != null) {
            entity.getAvailableIntakes().addAll(dto.getAvailableIntakes());
        }

        entity.getRequiredExams().clear();
        if (dto.getRequiredExams() != null) {
            entity.getRequiredExams().addAll(dto.getRequiredExams());
        }

        entity.getSubjects().clear();
        if (dto.getSubjects() != null) entity.getSubjects().addAll(dto.getSubjects());

        entity.getCareerProspects().clear();
        if (dto.getCareerProspects() != null) entity.getCareerProspects().addAll(dto.getCareerProspects());
    }
}