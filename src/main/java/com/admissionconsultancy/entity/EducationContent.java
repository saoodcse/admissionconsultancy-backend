package com.admissionconsultancy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "education_contents", indexes = {
        @Index(name = "idx_content_slug", columnList = "urlSlug"),
        @Index(name = "idx_content_category", columnList = "category")
})
public class EducationContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Content type is required")
    @Column(nullable = false)
    private String contentType;

    @NotBlank(message = "SEO title is required")
    @Column(nullable = false)
    private String seoTitle;

    @NotBlank(message = "Meta title is required")
    @Column(nullable = false)
    private String metaTitle;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String metaDescription;

    @NotBlank(message = "URL slug is required")
    @Column(unique = true, nullable = false)
    private String urlSlug;

    @NotBlank(message = "Primary keyword is required")
    @Column(nullable = false)
    private String primaryKeyword;

    @Column(columnDefinition = "TEXT")
    private String secondaryKeywords;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String articleContent;

    @Column(columnDefinition = "TEXT")
    private String faqs;

    private String category;
    private boolean active = true;

    // RELATIONAL TAGGING (Country, University, Course)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Country taggedCountry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private University taggedUniversity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Course taggedCourse;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}