package com.admissionconsultancy.repository;

import com.admissionconsultancy.entity.Lead;
import com.admissionconsultancy.enums.LeadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    // Admins usually want to see the newest leads first
    List<Lead> findAllByOrderByCreatedAtDesc();

    // Filter by status (e.g., fetch only NEW leads)
    List<Lead> findByStatusOrderByCreatedAtDesc(LeadStatus status);

    Optional<Lead> findByEmailAndPhone(String email, String phone);
}