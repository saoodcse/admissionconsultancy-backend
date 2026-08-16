package com.admissionconsultancy.repository;

import com.admissionconsultancy.entity.ExamRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRequirementRepository extends JpaRepository<ExamRequirement, Long> {

    // Lookup requirements mapped directly to a specific Course
    List<ExamRequirement> findByCourseSlug(String courseSlug);

    // Lookup requirements mapped at the College level
    List<ExamRequirement> findByCollegeSlug(String collegeSlug);

    // Lookup requirements mapped at the University level
    List<ExamRequirement> findByUniversitySlug(String universitySlug);

    // Lookup broad requirements mapped at the Country level
    List<ExamRequirement> findByCountrySlug(String countrySlug);

    @Query("SELECT er FROM ExamRequirement er " +
            "JOIN FETCH er.exam e " +
            "JOIN FETCH er.country c " +
            "LEFT JOIN FETCH er.university u " +
            "LEFT JOIN FETCH er.college col " +
            "LEFT JOIN FETCH er.course crs " +
            "WHERE c.slug = :countrySlug")
    List<ExamRequirement> findAllByCountrySlugWithDetails(@Param("countrySlug") String countrySlug);

    @Query("SELECT er FROM ExamRequirement er " +
            "JOIN FETCH er.exam e " +
            "JOIN FETCH er.country c " +
            "LEFT JOIN FETCH er.university u " +
            "LEFT JOIN FETCH er.college col " +
            "LEFT JOIN FETCH er.course crs " +
            "WHERE e.code = :examCode")
    List<ExamRequirement> findAllByExamCodeWithDetails(@Param("examCode") String examCode);
}