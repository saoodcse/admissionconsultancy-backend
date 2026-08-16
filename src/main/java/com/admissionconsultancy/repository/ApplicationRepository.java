package com.admissionconsultancy.repository;

import com.admissionconsultancy.entity.Application;
import com.admissionconsultancy.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findAllByApplicantEmailOrderByCreatedAtDesc(String applicantEmail);
    List<Application> findAllByStatusOrderByCreatedAtDesc(ApplicationStatus status);
}