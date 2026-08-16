package com.admissionconsultancy.repository;

import com.admissionconsultancy.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findBySlugAndIsActiveTrue(String slug);
    List<Country> findAllByIsActiveTrue();
    boolean existsBySlug(String slug);
    Optional<Country> findBySlug(String slug);
    List<Country> findByNameContainingIgnoreCaseAndIsActiveTrue(String query);
}
