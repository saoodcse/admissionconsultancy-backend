package com.admissionconsultancy.repository;

import com.admissionconsultancy.entity.ExamCountry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamCountryRepository extends JpaRepository<ExamCountry, Long> {

    // Used to check if an mapping already exists (ignoring active status)
    boolean existsByExamCodeAndCountrySlug(String examCode, String countrySlug);

    // Used by ExamRequirement validation to ensure the mapping is actually active
    boolean existsByExamCodeAndCountrySlugAndIsActiveTrue(String examCode, String countrySlug);

    // Helpful for finding all exams applicable in a specific country
    List<ExamCountry> findByCountrySlugAndIsActiveTrue(String countrySlug);

    // Helpful for finding all countries where a specific exam is applicable
    List<ExamCountry> findByExamCodeAndIsActiveTrue(String examCode);

    @Query("SELECT ec FROM ExamCountry ec JOIN FETCH ec.exam WHERE ec.country.slug = :countrySlug AND ec.isActive = true AND ec.exam.isActive = true")
    List<ExamCountry> findActiveExamsByCountrySlug(@Param("countrySlug") String countrySlug);

    @Query("SELECT ec FROM ExamCountry ec JOIN FETCH ec.country WHERE ec.exam.code = :examCode AND ec.isActive = true AND ec.country.isActive = true")
    List<ExamCountry> findActiveCountriesByExamCode(@Param("examCode") String examCode);
}