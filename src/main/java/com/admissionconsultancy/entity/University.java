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
@Table(name = "universities")
public class University {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "University name cannot be blank")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Slug cannot be blank")
    @Column(nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer established;
    private String type;
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
    @CollectionTable(name = "university_available_intakes", joinColumns = @JoinColumn(name = "university_id"))
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

    // RELATIONAL LINK TO COURSES (Many-to-Many)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "university_course_mapping",
            joinColumns = @JoinColumn(name = "university_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Course> offeredCourses = new ArrayList<>();

    // RELATIONAL LINK TO COLLEGES (One-to-Many)
    @OneToMany(mappedBy = "university", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<College> colleges = new ArrayList<>();

    // PRIMITIVE COLLECTIONS
    @ElementCollection
    @CollectionTable(name = "university_accreditations", joinColumns = @JoinColumn(name = "university_id"))
    @Column(name = "accreditation")
    private List<String> accreditation = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "university_facilities", joinColumns = @JoinColumn(name = "university_id"))
    @Column(name = "facility")
    private List<String> facilities = new ArrayList<>();
}