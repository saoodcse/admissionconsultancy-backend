package com.admissionconsultancy.repository;

import com.admissionconsultancy.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    boolean existsByCode(String code);

    Optional<Exam> findByCodeAndIsActiveTrue(String code);
    
    Optional<Exam> findByCode(String code);

    List<Exam> findAllByIsActiveTrue();
}