package com.admissionconsultancy.repository;

import com.admissionconsultancy.entity.College;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollegeRepository extends JpaRepository<College, Long> {
    List<College> findAllByIsActiveTrue();
    Optional<College> findBySlugAndIsActiveTrue(String slug);
    Optional<College> findBySlug(String slug);
    List<College> findByCountrySlugAndIsActiveTrue(String countrySlug);
    boolean existsBySlug(String slug);
    List<College> findByNameContainingIgnoreCaseAndIsActiveTrue(String query);
}