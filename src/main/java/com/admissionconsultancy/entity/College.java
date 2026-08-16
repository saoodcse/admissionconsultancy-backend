package com.admissionconsultancy.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "colleges")
public class College {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "College name cannot be blank")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Slug cannot be blank")
    @Column(nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer established;
    private String type; // e.g., Private, Public
    private Integer ranking;

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

    // Admissions & Student Life
    @ElementCollection
    @CollectionTable(name = "college_available_intakes", joinColumns = @JoinColumn(name = "college_id"))
    @Column(name = "intake")
    private List<String> availableIntakes = new ArrayList<>();

    private String applicationDeadline;
    private String baselineEligibility;
    private Boolean hasOnCampusHostel;
    private Boolean hasInternationalMess;
    private Double estimatedMonthlyLivingUsd;

    private String image;
    private String logo;

    private boolean isActive = true;
    private Integer popularityScore = 0;
    private Integer viewCount = 0;
    private Double rating = 0.0;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // RELATIONAL LINK TO COUNTRY (Many-to-One)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Country country;

    // RELATIONAL LINK TO UNIVERSITY (Many-to-One)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", nullable = true)
    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private University university;

    // RELATIONAL LINK TO COURSES (Many-to-Many)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "college_course_mapping",
            joinColumns = @JoinColumn(name = "college_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Course> offeredCourses = new ArrayList<>();

    // PRIMITIVE COLLECTIONS

    @ElementCollection
    @CollectionTable(name = "college_accreditations", joinColumns = @JoinColumn(name = "college_id"))
    @Column(name = "accreditation")
    private List<String> accreditation = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "college_facilities", joinColumns = @JoinColumn(name = "college_id"))
    @Column(name = "facility")
    private List<String> facilities = new ArrayList<>();
}