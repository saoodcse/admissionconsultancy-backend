package com.admissionconsultancy.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CountryDTO {

    @NotBlank(message = "Country name cannot be blank")
    private String name;

    @NotBlank(message = "Slug cannot be blank")
    private String slug;

    private String flag;
    private String banner;
    private String heroTitle;
    private String heroSubtitle;

    // SEO & OpenGraph Meta Tags
    private String metaTitle;
    private String metaDescription;
    private String keywords;
    private String ogTitle;
    private String ogDescription;
    private String ogImage;
    private String canonicalUrl;

    private String overview;

    // Section Titles and Intros
    private String whyStudyTitle;
    private String whyStudyIntro;
    private String eligibilityTitle;
    private String eligibilityIntro;
    private String thingsToConsiderTitle;
    private String thingsToConsiderIntro;
    private String admissionProcessTitle;
    private String admissionProcessIntro;
    private String documentsTitle;
    private String syllabusTitle;
    private String syllabusIntro;
    private String fmgeTitle;
    private String fmgeIntro;
    private String fmgeNote;
    private String feeStructureTitle;
    private String feeStructureIntro;
    private String livingCostTitle;
    private String livingCostIntro;
    private String scholarshipTitle;
    private String scholarshipIntro;
    private String hostelTitle;
    private String hostelIntro;
    private String topUniversitiesTitle;
    private String topUniversitiesIntro;
    private String benefitsTitle;
    private String nmcApprovedTitle;
    private String aboutCountryTitle;
    private String aboutCountryContent;
    private String popularDestinationsTitle;
    private String faqTitle;

    // Global Admissions, Legal & Currency Filtering Metadata
    private String postStudyWorkRights;
    private String licensingExamNote;
    private Boolean isPrPathAvailable;
    private Double minAnnualTuitionUsd;
    private Double maxAnnualTuitionUsd;
    private Double avgMonthlyLivingUsd;
    private List<String> availableIntakes;
    private String applicationDeadlineNote;

    // Global Compliance, Language & Student Life
    private List<String> acceptedLanguageTests;
    private String minimumLanguageScoreNote;
    private String visaSuccessRate;
    private Boolean requiresBlockedAccount;
    private String partTimeWorkAllowedHours;
    private String healthInsuranceRequirement;
    private String climateOverview;
    private String totalInternationalStudents;
    private String safetyIndexRating;

    private boolean showGetFreeConsultation;
    private boolean showConnectWithCounselor;
    private boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // NESTED DTO LISTS & PRIMITIVE LISTS
    private List<UniversitySummaryDTO> universities;
    private List<CountryHighlightDTO> highlights;
    private List<CountryWhyStudyPointDTO> whyStudyPoints;
    private List<CountryEligibilityDTO> eligibilityTable;
    private List<String> thingsToConsiderPoints;
    private List<CountryAdmissionStepDTO> admissionProcessSteps;
    private List<String> documentsList;
    private List<CountrySemesterDTO> syllabusSemesters;
    private List<CountryFmgePassRateDTO> fmgePassRates;
    private List<CountryFeeStructureDTO> feeStructureRows;
    private List<CountryLivingCostDTO> livingCostRows;
    private List<CountryScholarshipDTO> scholarshipTypes;
    private List<CountryHostelFacilityDTO> hostelFacilities;
    private List<String> benefitsList;
    private List<CountryAboutAttributeDTO> aboutCountryTable;
    private List<CountryDestinationDTO> popularDestinations;
    private List<CountryFaqDTO> faqs;
    private List<String> galleryImages;
}