package com.admissionconsultancy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Course name cannot be blank")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Slug cannot be blank")
    @Column(nullable = false, unique = true)
    private String slug;

    private String fullName;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String duration;
    private String degreeType;
    private String studyLevel;
    private String field;

    @Column(columnDefinition = "TEXT")
    private String eligibility;

    // Pricing & Currency
    private Double minTuitionFee;
    private Double maxTuitionFee;
    private String feeCurrency;
    private String tuitionFeeNative;
    private Double minTuitionUsd;
    private Double maxTuitionUsd;

    // SEO & OpenGraph Meta Tags
    private String metaTitle;
    @Column(columnDefinition = "TEXT")
    private String metaDescription;
    private String keywords;
    private String ogImage;

    // Admissions, Intakes & Exams
    @ElementCollection
    @CollectionTable(name = "course_available_intakes", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "intake")
    private List<String> availableIntakes = new ArrayList<>();

    private String applicationDeadline;

    @ElementCollection
    @CollectionTable(name = "course_required_exams", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "exam_name")
    private List<String> requiredExams = new ArrayList<>();

    private String image;

    // Status and Metrics
    private boolean isActive = true;
    private Integer popularityScore = 0;
    private Integer viewCount = 0;
    private Double rating = 0.0;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // RELATIONAL LINK TO UNIVERSITIES (Many-to-Many)
    @ManyToMany(mappedBy = "offeredCourses", fetch = FetchType.LAZY)
    @JsonIgnore // Prevent huge cyclical JSON payloads if ever serialized directly
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<University> universities = new ArrayList<>();

    // RELATIONAL LINK TO COLLEGES (Many-to-Many)
    @ManyToMany(mappedBy = "offeredCourses", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<College> colleges = new ArrayList<>();

    // PRIMITIVE COLLECTIONS
    @ElementCollection
    @CollectionTable(name = "course_subjects", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "subject")
    private List<String> subjects = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "course_career_prospects", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "prospect")
    private List<String> careerProspects = new ArrayList<>();
}