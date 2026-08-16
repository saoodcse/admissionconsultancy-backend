package com.admissionconsultancy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "exams")
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Exam name cannot be blank")
    @Column(nullable = false, unique = true)
    private String name; // e.g., "IELTS"

    @NotBlank(message = "Exam code cannot be blank")
    @Column(nullable = false, unique = true)
    private String code; // e.g., "IELTS_ACADEMIC"

    @Column(columnDefinition = "TEXT")
    private String description;

    // SEO & OpenGraph Meta Tags
    private String metaTitle;
    @Column(columnDefinition = "TEXT")
    private String metaDescription;
    private String keywords;
    private String ogImage;

    // Testing & Administration Details
    private String conductingBody;
    private String examMode;
    private String scoreValidity;
    private String officialWebsiteUrl;

    private boolean isActive = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}