package com.admissionconsultancy.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "countries")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Country name cannot be blank")
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank(message = "Slug cannot be blank")
    @Column(nullable = false, unique = true)
    private String slug;

    private String flag;
    private String banner;
    private String heroTitle;
    private String heroSubtitle;

    // SEO & OpenGraph Meta Tags
    private String metaTitle;
    @Column(columnDefinition = "TEXT")
    private String metaDescription;
    private String keywords;
    private String ogTitle;
    @Column(columnDefinition = "TEXT")
    private String ogDescription;
    private String ogImage;
    private String canonicalUrl;

    @Column(columnDefinition = "TEXT")
    private String overview;

    // Section Headers & Intros
    private String whyStudyTitle;
    @Column(columnDefinition = "TEXT") private String whyStudyIntro;
    private String eligibilityTitle;
    @Column(columnDefinition = "TEXT") private String eligibilityIntro;
    private String thingsToConsiderTitle;
    @Column(columnDefinition = "TEXT") private String thingsToConsiderIntro;
    private String admissionProcessTitle;
    @Column(columnDefinition = "TEXT") private String admissionProcessIntro;
    private String documentsTitle;
    private String syllabusTitle;
    @Column(columnDefinition = "TEXT") private String syllabusIntro;
    private String fmgeTitle;
    @Column(columnDefinition = "TEXT") private String fmgeIntro;
    private String fmgeNote;
    private String feeStructureTitle;
    @Column(columnDefinition = "TEXT") private String feeStructureIntro;
    private String livingCostTitle;
    @Column(columnDefinition = "TEXT") private String livingCostIntro;
    private String scholarshipTitle;
    @Column(columnDefinition = "TEXT") private String scholarshipIntro;
    private String hostelTitle;
    @Column(columnDefinition = "TEXT") private String hostelIntro;
    private String topUniversitiesTitle;
    @Column(columnDefinition = "TEXT") private String topUniversitiesIntro;
    private String benefitsTitle;
    private String nmcApprovedTitle;
    private String aboutCountryTitle;
    @Column(columnDefinition = "TEXT") private String aboutCountryContent;
    private String popularDestinationsTitle;
    private String faqTitle;

    // Global Admissions, Legal & Currency Filtering Metadata
    private String postStudyWorkRights;
    private String licensingExamNote;
    private Boolean isPrPathAvailable;
    private Double minAnnualTuitionUsd;
    private Double maxAnnualTuitionUsd;
    private Double avgMonthlyLivingUsd;

    @ElementCollection
    @CollectionTable(name = "country_available_intakes", joinColumns = @JoinColumn(name = "country_id"))
    @Column(name = "intake")
    private List<String> availableIntakes = new ArrayList<>();

    private String applicationDeadlineNote;

    // Global Compliance, Language & Student Life
    @ElementCollection
    @CollectionTable(name = "country_accepted_language_tests", joinColumns = @JoinColumn(name = "country_id"))
    @Column(name = "test_name")
    private List<String> acceptedLanguageTests = new ArrayList<>();

    private String minimumLanguageScoreNote;
    private String visaSuccessRate;
    private Boolean requiresBlockedAccount;
    private String partTimeWorkAllowedHours;
    private String healthInsuranceRequirement;
    private String climateOverview;
    private String totalInternationalStudents;
    private String safetyIndexRating;

    private boolean showGetFreeConsultation = true;
    private boolean showConnectWithCounselor = true;
    private boolean isActive = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // RELATIONAL LINK TO UNIVERSITIES

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<University> universities = new ArrayList<>();

    // PRIMITIVE COLLECTIONS (Strings)

    @ElementCollection
    @CollectionTable(name = "country_things_to_consider", joinColumns = @JoinColumn(name = "country_id"))
    @Column(name = "point", columnDefinition = "TEXT")
    private List<String> thingsToConsiderPoints = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "country_documents", joinColumns = @JoinColumn(name = "country_id"))
    @Column(name = "document")
    private List<String> documentsList = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "country_benefits", joinColumns = @JoinColumn(name = "country_id"))
    @Column(name = "benefit", columnDefinition = "TEXT")
    private List<String> benefitsList = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "country_gallery", joinColumns = @JoinColumn(name = "country_id"))
    @Column(name = "image_url", columnDefinition = "TEXT")
    private List<String> galleryImages = new ArrayList<>();

    // COMPLEX COLLECTIONS (Child Entities)

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<CountryHighlight> highlights = new ArrayList<>();

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<CountryWhyStudyPoint> whyStudyPoints = new ArrayList<>();

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<CountryEligibility> eligibilityTable = new ArrayList<>();

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<CountryAdmissionStep> admissionProcessSteps = new ArrayList<>();

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<CountrySemester> syllabusSemesters = new ArrayList<>();

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<CountryFmgePassRate> fmgePassRates = new ArrayList<>();

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<CountryFeeStructure> feeStructureRows = new ArrayList<>();

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<CountryLivingCost> livingCostRows = new ArrayList<>();

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<CountryScholarship> scholarshipTypes = new ArrayList<>();

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<CountryHostelFacility> hostelFacilities = new ArrayList<>();

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<CountryAboutAttribute> aboutCountryTable = new ArrayList<>();

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<CountryDestination> popularDestinations = new ArrayList<>();

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<CountryFaq> faqs = new ArrayList<>();

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<College> colleges = new ArrayList<>();


    // Helper method to sync bidirectional relationships before saving
    public void syncChildren() {
        if (universities != null) universities.forEach(u -> u.setCountry(this));
        if (colleges != null) colleges.forEach(c -> c.setCountry(this));
        if (highlights != null) highlights.forEach(h -> h.setCountry(this));
        if (whyStudyPoints != null) whyStudyPoints.forEach(w -> w.setCountry(this));
        if (eligibilityTable != null) eligibilityTable.forEach(e -> e.setCountry(this));
        if (admissionProcessSteps != null) admissionProcessSteps.forEach(a -> a.setCountry(this));
        if (syllabusSemesters != null) syllabusSemesters.forEach(s -> s.setCountry(this));
        if (fmgePassRates != null) fmgePassRates.forEach(f -> f.setCountry(this));
        if (feeStructureRows != null) feeStructureRows.forEach(f -> f.setCountry(this));
        if (livingCostRows != null) livingCostRows.forEach(l -> l.setCountry(this));
        if (scholarshipTypes != null) scholarshipTypes.forEach(s -> s.setCountry(this));
        if (hostelFacilities != null) hostelFacilities.forEach(h -> h.setCountry(this));
        if (aboutCountryTable != null) aboutCountryTable.forEach(a -> a.setCountry(this));
        if (popularDestinations != null) popularDestinations.forEach(p -> p.setCountry(this));
        if (faqs != null) faqs.forEach(f -> f.setCountry(this));
    }
}