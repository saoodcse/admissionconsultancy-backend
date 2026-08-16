package com.admissionconsultancy.repository;

import com.admissionconsultancy.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UniversityRepository extends JpaRepository<University, Long>, JpaSpecificationExecutor<University> {
    List<University> findAllByIsActiveTrue();
    Optional<University> findBySlugAndIsActiveTrue(String slug);
    Optional<University> findBySlug(String slug);
    List<University> findByCountrySlugAndIsActiveTrue(String countrySlug);
    boolean existsBySlug(String slug);
    List<University> findByNameContainingIgnoreCaseAndIsActiveTrue(String query);

    List<University> findAllBySlugIn(List<String> universitySlugs);
}