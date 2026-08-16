package com.admissionconsultancy.repository;

import com.admissionconsultancy.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    Optional<AdminUser> findByEmailAndIsActiveTrue(String email);
    boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE AdminUser u SET u.isActive = false WHERE u.email = :email")
    int deactivateByEmail(@Param("email") String email);
}