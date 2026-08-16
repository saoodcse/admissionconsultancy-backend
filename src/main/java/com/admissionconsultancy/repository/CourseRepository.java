package com.admissionconsultancy.repository;

import com.admissionconsultancy.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findAllByIsActiveTrue();
    Optional<Course> findBySlugAndIsActiveTrue(String slug);
    Optional<Course> findBySlug(String slug);
    boolean existsBySlug(String slug);
    void deleteBySlug(String slug);
    List<Course> findByNameContainingIgnoreCaseAndIsActiveTrue(String query);
    List<Course> findBySlugIn(List<String> slugs);

}