package com.admissionconsultancy.repository;

import com.admissionconsultancy.entity.EducationContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EducationContentRepository extends JpaRepository<EducationContent, Long>, JpaSpecificationExecutor<EducationContent> {
    List<EducationContent> findAllByActiveTrue();
    Optional<EducationContent> findByUrlSlugAndActiveTrue(String urlSlug);
    Optional<EducationContent> findByUrlSlug(String urlSlug);
    boolean existsByUrlSlugAndActiveTrue(String urlSlug); // Fixed soft-delete conflict check
    List<EducationContent> findByCategoryAndActiveTrue(String category);

    // Tag Filtering Queries
    List<EducationContent> findByTaggedCountrySlugAndActiveTrue(String countrySlug);
    List<EducationContent> findByTaggedUniversitySlugAndActiveTrue(String universitySlug);
    List<EducationContent> findByTaggedCourseSlugAndActiveTrue(String courseSlug);
}