package com.admissionconsultancy.mapper;

import com.admissionconsultancy.dto.*;
import com.admissionconsultancy.entity.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public final class CountryMapper {

    // ENTITY -> DTO

    public CountryDTO toDTO(Country entity) {
        if (entity == null) return null;

        CountryDTO dto = new CountryDTO();

        // Basic Fields
        dto.setName(entity.getName());
        dto.setSlug(entity.getSlug());
        dto.setFlag(entity.getFlag());
        dto.setBanner(entity.getBanner());
        dto.setHeroTitle(entity.getHeroTitle());
        dto.setHeroSubtitle(entity.getHeroSubtitle());

        // SEO & OpenGraph Meta Tags
        dto.setMetaTitle(entity.getMetaTitle());
        dto.setMetaDescription(entity.getMetaDescription());
        dto.setKeywords(entity.getKeywords());
        dto.setOgTitle(entity.getOgTitle());
        dto.setOgDescription(entity.getOgDescription());
        dto.setOgImage(entity.getOgImage());
        dto.setCanonicalUrl(entity.getCanonicalUrl());

        dto.setOverview(entity.getOverview());

        // Section Titles and Intros
        dto.setWhyStudyTitle(entity.getWhyStudyTitle());
        dto.setWhyStudyIntro(entity.getWhyStudyIntro());
        dto.setEligibilityTitle(entity.getEligibilityTitle());
        dto.setEligibilityIntro(entity.getEligibilityIntro());
        dto.setThingsToConsiderTitle(entity.getThingsToConsiderTitle());
        dto.setThingsToConsiderIntro(entity.getThingsToConsiderIntro());
        dto.setAdmissionProcessTitle(entity.getAdmissionProcessTitle());
        dto.setAdmissionProcessIntro(entity.getAdmissionProcessIntro());
        dto.setDocumentsTitle(entity.getDocumentsTitle());
        dto.setSyllabusTitle(entity.getSyllabusTitle());
        dto.setSyllabusIntro(entity.getSyllabusIntro());
        dto.setFmgeTitle(entity.getFmgeTitle());
        dto.setFmgeIntro(entity.getFmgeIntro());
        dto.setFmgeNote(entity.getFmgeNote());
        dto.setFeeStructureTitle(entity.getFeeStructureTitle());
        dto.setFeeStructureIntro(entity.getFeeStructureIntro());
        dto.setLivingCostTitle(entity.getLivingCostTitle());
        dto.setLivingCostIntro(entity.getLivingCostIntro());
        dto.setScholarshipTitle(entity.getScholarshipTitle());
        dto.setScholarshipIntro(entity.getScholarshipIntro());
        dto.setHostelTitle(entity.getHostelTitle());
        dto.setHostelIntro(entity.getHostelIntro());
        dto.setTopUniversitiesTitle(entity.getTopUniversitiesTitle());
        dto.setTopUniversitiesIntro(entity.getTopUniversitiesIntro());
        dto.setBenefitsTitle(entity.getBenefitsTitle());
        dto.setNmcApprovedTitle(entity.getNmcApprovedTitle());
        dto.setAboutCountryTitle(entity.getAboutCountryTitle());
        dto.setAboutCountryContent(entity.getAboutCountryContent());
        dto.setPopularDestinationsTitle(entity.getPopularDestinationsTitle());
        dto.setFaqTitle(entity.getFaqTitle());

        // Global Admissions, Legal & Currency Filtering Metadata
        dto.setPostStudyWorkRights(entity.getPostStudyWorkRights());
        dto.setLicensingExamNote(entity.getLicensingExamNote());
        dto.setIsPrPathAvailable(entity.getIsPrPathAvailable());
        dto.setMinAnnualTuitionUsd(entity.getMinAnnualTuitionUsd());
        dto.setMaxAnnualTuitionUsd(entity.getMaxAnnualTuitionUsd());
        dto.setAvgMonthlyLivingUsd(entity.getAvgMonthlyLivingUsd());
        if (entity.getAvailableIntakes() != null) {
            dto.setAvailableIntakes(new ArrayList<>(entity.getAvailableIntakes()));
        }
        dto.setApplicationDeadlineNote(entity.getApplicationDeadlineNote());

        // Global Compliance, Language & Student Life
        if (entity.getAcceptedLanguageTests() != null) {
            dto.setAcceptedLanguageTests(new ArrayList<>(entity.getAcceptedLanguageTests()));
        }
        dto.setMinimumLanguageScoreNote(entity.getMinimumLanguageScoreNote());
        dto.setVisaSuccessRate(entity.getVisaSuccessRate());
        dto.setRequiresBlockedAccount(entity.getRequiresBlockedAccount());
        dto.setPartTimeWorkAllowedHours(entity.getPartTimeWorkAllowedHours());
        dto.setHealthInsuranceRequirement(entity.getHealthInsuranceRequirement());
        dto.setClimateOverview(entity.getClimateOverview());
        dto.setTotalInternationalStudents(entity.getTotalInternationalStudents());
        dto.setSafetyIndexRating(entity.getSafetyIndexRating());

        // Booleans & Dates
        dto.setShowGetFreeConsultation(entity.isShowGetFreeConsultation());
        dto.setShowConnectWithCounselor(entity.isShowConnectWithCounselor());
        dto.setActive(entity.isActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // Primitive Collections
        if (entity.getThingsToConsiderPoints() != null) {
            dto.setThingsToConsiderPoints(new ArrayList<>(entity.getThingsToConsiderPoints()));
        }
        if (entity.getDocumentsList() != null) {
            dto.setDocumentsList(new ArrayList<>(entity.getDocumentsList()));
        }
        if (entity.getBenefitsList() != null) {
            dto.setBenefitsList(new ArrayList<>(entity.getBenefitsList()));
        }
        if (entity.getGalleryImages() != null) {
            dto.setGalleryImages(new ArrayList<>(entity.getGalleryImages()));
        }

        // Object Collections
        if (entity.getHighlights() != null) {
            dto.setHighlights(entity.getHighlights().stream().map(e -> {
                CountryHighlightDTO d = new CountryHighlightDTO();
                d.setFeature(e.getFeature());
                d.setDetail(e.getDetail());
                return d;
            }).collect(Collectors.toList()));
        }

        if (entity.getWhyStudyPoints() != null) {
            dto.setWhyStudyPoints(entity.getWhyStudyPoints().stream().map(e -> {
                CountryWhyStudyPointDTO d = new CountryWhyStudyPointDTO();
                d.setTitle(e.getTitle());
                d.setContent(e.getContent());
                return d;
            }).collect(Collectors.toList()));
        }

        if (entity.getEligibilityTable() != null) {
            dto.setEligibilityTable(entity.getEligibilityTable().stream().map(e -> {
                CountryEligibilityDTO d = new CountryEligibilityDTO();
                d.setDetail(e.getDetail());
                d.setRequirement(e.getRequirement());
                return d;
            }).collect(Collectors.toList()));
        }

        if (entity.getAdmissionProcessSteps() != null) {
            dto.setAdmissionProcessSteps(entity.getAdmissionProcessSteps().stream().map(e -> {
                CountryAdmissionStepDTO d = new CountryAdmissionStepDTO();
                d.setStepOrder(e.getStepOrder());
                d.setTitle(e.getTitle());
                d.setContent(e.getContent());
                return d;
            }).collect(Collectors.toList()));
        }

        if (entity.getSyllabusSemesters() != null) {
            dto.setSyllabusSemesters(entity.getSyllabusSemesters().stream().map(e -> {
                CountrySemesterDTO d = new CountrySemesterDTO();
                d.setSemester(e.getSemester());
                if (e.getSubjects() != null) d.setSubjects(new ArrayList<>(e.getSubjects()));
                return d;
            }).collect(Collectors.toList()));
        }

        if (entity.getFmgePassRates() != null) {
            dto.setFmgePassRates(entity.getFmgePassRates().stream().map(e -> {
                CountryFmgePassRateDTO d = new CountryFmgePassRateDTO();
                d.setUniversity(e.getUniversity());
                d.setPassRate(e.getPassRate());
                return d;
            }).collect(Collectors.toList()));
        }

        if (entity.getFeeStructureRows() != null) {
            dto.setFeeStructureRows(entity.getFeeStructureRows().stream().map(e -> {
                CountryFeeStructureDTO d = new CountryFeeStructureDTO();
                d.setUniversityName(e.getUniversityName());
                d.setTuitionFeeNative(e.getTuitionFeeNative());
                d.setHostelFeeNative(e.getHostelFeeNative());
                d.setNativeCurrencyCode(e.getNativeCurrencyCode());
                d.setAnnualTuitionUsd(e.getAnnualTuitionUsd());
                d.setAnnualHostelUsd(e.getAnnualHostelUsd());
                return d;
            }).collect(Collectors.toList()));
        }

        if (entity.getLivingCostRows() != null) {
            dto.setLivingCostRows(entity.getLivingCostRows().stream().map(e -> {
                CountryLivingCostDTO d = new CountryLivingCostDTO();
                d.setExpenseCategory(e.getExpenseCategory());
                d.setMonthlyCost(e.getMonthlyCost());
                return d;
            }).collect(Collectors.toList()));
        }

        if (entity.getScholarshipTypes() != null) {
            dto.setScholarshipTypes(entity.getScholarshipTypes().stream().map(e -> {
                CountryScholarshipDTO d = new CountryScholarshipDTO();
                d.setName(e.getName());
                d.setDescription(e.getDescription());
                return d;
            }).collect(Collectors.toList()));
        }

        if (entity.getHostelFacilities() != null) {
            dto.setHostelFacilities(entity.getHostelFacilities().stream().map(e -> {
                CountryHostelFacilityDTO d = new CountryHostelFacilityDTO();
                d.setCriteria(e.getCriteria());
                d.setInformation(e.getInformation());
                return d;
            }).collect(Collectors.toList()));
        }

        if (entity.getAboutCountryTable() != null) {
            dto.setAboutCountryTable(entity.getAboutCountryTable().stream().map(e -> {
                CountryAboutAttributeDTO d = new CountryAboutAttributeDTO();
                d.setAttribute(e.getAttribute());
                d.setDetail(e.getDetail());
                return d;
            }).collect(Collectors.toList()));
        }

        if (entity.getPopularDestinations() != null) {
            dto.setPopularDestinations(entity.getPopularDestinations().stream().map(e -> {
                CountryDestinationDTO d = new CountryDestinationDTO();
                d.setName(e.getName());
                d.setSlug(e.getSlug());
                d.setFlag(e.getFlag());
                return d;
            }).collect(Collectors.toList()));
        }

        if (entity.getFaqs() != null) {
            dto.setFaqs(entity.getFaqs().stream().map(e -> {
                CountryFaqDTO d = new CountryFaqDTO();
                d.setQuestion(e.getQuestion());
                d.setAnswer(e.getAnswer());
                return d;
            }).collect(Collectors.toList()));
        }

        // Map Universities to UniversitySummaryDTO list
        if (entity.getUniversities() != null) {
            dto.setUniversities(entity.getUniversities().stream().map(uni -> {
                UniversitySummaryDTO summary = new UniversitySummaryDTO();
                summary.setName(uni.getName());
                summary.setSlug(uni.getSlug());
                summary.setLogo(uni.getLogo());
                summary.setCountryName(entity.getName());
                return summary;
            }).collect(Collectors.toList()));
        }

        return dto;
    }

    // DTO -> ENTITY (For Creates & Updates)

    public void updateEntityFromDTO(CountryDTO dto, Country entity) {
        if (dto == null || entity == null) return;

        // Basic Fields
        entity.setName(dto.getName());
        entity.setSlug(dto.getSlug());
        entity.setFlag(dto.getFlag());
        entity.setBanner(dto.getBanner());
        entity.setHeroTitle(dto.getHeroTitle());
        entity.setHeroSubtitle(dto.getHeroSubtitle());

        // SEO & OpenGraph Meta Tags
        entity.setMetaTitle(dto.getMetaTitle());
        entity.setMetaDescription(dto.getMetaDescription());
        entity.setKeywords(dto.getKeywords());
        entity.setOgTitle(dto.getOgTitle());
        entity.setOgDescription(dto.getOgDescription());
        entity.setOgImage(dto.getOgImage());
        entity.setCanonicalUrl(dto.getCanonicalUrl());

        entity.setOverview(dto.getOverview());

        // Title and Intro Fields
        entity.setWhyStudyTitle(dto.getWhyStudyTitle());
        entity.setWhyStudyIntro(dto.getWhyStudyIntro());
        entity.setEligibilityTitle(dto.getEligibilityTitle());
        entity.setEligibilityIntro(dto.getEligibilityIntro());
        entity.setThingsToConsiderTitle(dto.getThingsToConsiderTitle());
        entity.setThingsToConsiderIntro(dto.getThingsToConsiderIntro());
        entity.setAdmissionProcessTitle(dto.getAdmissionProcessTitle());
        entity.setAdmissionProcessIntro(dto.getAdmissionProcessIntro());
        entity.setDocumentsTitle(dto.getDocumentsTitle());
        entity.setSyllabusTitle(dto.getSyllabusTitle());
        entity.setSyllabusIntro(dto.getSyllabusIntro());
        entity.setFmgeTitle(dto.getFmgeTitle());
        entity.setFmgeIntro(dto.getFmgeIntro());
        entity.setFmgeNote(dto.getFmgeNote());
        entity.setFeeStructureTitle(dto.getFeeStructureTitle());
        entity.setFeeStructureIntro(dto.getFeeStructureIntro());
        entity.setLivingCostTitle(dto.getLivingCostTitle());
        entity.setLivingCostIntro(dto.getLivingCostIntro());
        entity.setScholarshipTitle(dto.getScholarshipTitle());
        entity.setScholarshipIntro(dto.getScholarshipIntro());
        entity.setHostelTitle(dto.getHostelTitle());
        entity.setHostelIntro(dto.getHostelIntro());
        entity.setTopUniversitiesTitle(dto.getTopUniversitiesTitle());
        entity.setTopUniversitiesIntro(dto.getTopUniversitiesIntro());
        entity.setBenefitsTitle(dto.getBenefitsTitle());
        entity.setNmcApprovedTitle(dto.getNmcApprovedTitle());
        entity.setAboutCountryTitle(dto.getAboutCountryTitle());
        entity.setAboutCountryContent(dto.getAboutCountryContent());
        entity.setPopularDestinationsTitle(dto.getPopularDestinationsTitle());
        entity.setFaqTitle(dto.getFaqTitle());

        // Global Admissions, Legal & Currency Filtering Metadata
        entity.setPostStudyWorkRights(dto.getPostStudyWorkRights());
        entity.setLicensingExamNote(dto.getLicensingExamNote());
        entity.setIsPrPathAvailable(dto.getIsPrPathAvailable());
        entity.setMinAnnualTuitionUsd(dto.getMinAnnualTuitionUsd());
        entity.setMaxAnnualTuitionUsd(dto.getMaxAnnualTuitionUsd());
        entity.setAvgMonthlyLivingUsd(dto.getAvgMonthlyLivingUsd());
        entity.getApplicationDeadlineNote(); // handled below

        // Global Compliance, Language & Student Life
        entity.setMinimumLanguageScoreNote(dto.getMinimumLanguageScoreNote());
        entity.setVisaSuccessRate(dto.getVisaSuccessRate());
        entity.setRequiresBlockedAccount(dto.getRequiresBlockedAccount());
        entity.setPartTimeWorkAllowedHours(dto.getPartTimeWorkAllowedHours());
        entity.setHealthInsuranceRequirement(dto.getHealthInsuranceRequirement());
        entity.setClimateOverview(dto.getClimateOverview());
        entity.setTotalInternationalStudents(dto.getTotalInternationalStudents());
        entity.setSafetyIndexRating(dto.getSafetyIndexRating());
        entity.setApplicationDeadlineNote(dto.getApplicationDeadlineNote());

        // Booleans
        entity.setShowGetFreeConsultation(dto.isShowGetFreeConsultation());
        entity.setShowConnectWithCounselor(dto.isShowConnectWithCounselor());
        entity.setActive(dto.isActive());

        // Primitive Collections
        entity.getThingsToConsiderPoints().clear();
        if (dto.getThingsToConsiderPoints() != null) {
            entity.getThingsToConsiderPoints().addAll(dto.getThingsToConsiderPoints());
        }

        entity.getDocumentsList().clear();
        if (dto.getDocumentsList() != null) {
            entity.getDocumentsList().addAll(dto.getDocumentsList());
        }

        entity.getBenefitsList().clear();
        if (dto.getBenefitsList() != null) {
            entity.getBenefitsList().addAll(dto.getBenefitsList());
        }

        entity.getGalleryImages().clear();
        if (dto.getGalleryImages() != null) {
            entity.getGalleryImages().addAll(dto.getGalleryImages());
        }

        entity.getAvailableIntakes().clear();
        if (dto.getAvailableIntakes() != null) {
            entity.getAvailableIntakes().addAll(dto.getAvailableIntakes());
        }

        entity.getAcceptedLanguageTests().clear();
        if (dto.getAcceptedLanguageTests() != null) {
            entity.getAcceptedLanguageTests().addAll(dto.getAcceptedLanguageTests());
        }

        // Clear and Rebuild Relational Collections

        entity.getHighlights().clear();
        if (dto.getHighlights() != null) {
            dto.getHighlights().forEach(d -> {
                CountryHighlight e = new CountryHighlight();
                e.setFeature(d.getFeature());
                e.setDetail(d.getDetail());
                e.setCountry(entity);
                entity.getHighlights().add(e);
            });
        }

        entity.getWhyStudyPoints().clear();
        if (dto.getWhyStudyPoints() != null) {
            dto.getWhyStudyPoints().forEach(d -> {
                CountryWhyStudyPoint e = new CountryWhyStudyPoint();
                e.setTitle(d.getTitle());
                e.setContent(d.getContent());
                e.setCountry(entity);
                entity.getWhyStudyPoints().add(e);
            });
        }

        entity.getEligibilityTable().clear();
        if (dto.getEligibilityTable() != null) {
            dto.getEligibilityTable().forEach(d -> {
                CountryEligibility e = new CountryEligibility();
                e.setDetail(d.getDetail());
                e.setRequirement(d.getRequirement());
                e.setCountry(entity);
                entity.getEligibilityTable().add(e);
            });
        }

        entity.getAdmissionProcessSteps().clear();
        if (dto.getAdmissionProcessSteps() != null) {
            dto.getAdmissionProcessSteps().forEach(d -> {
                CountryAdmissionStep e = new CountryAdmissionStep();
                e.setStepOrder(d.getStepOrder());
                e.setTitle(d.getTitle());
                e.setContent(d.getContent());
                e.setCountry(entity);
                entity.getAdmissionProcessSteps().add(e);
            });
        }

        entity.getSyllabusSemesters().clear();
        if (dto.getSyllabusSemesters() != null) {
            dto.getSyllabusSemesters().forEach(d -> {
                CountrySemester e = new CountrySemester();
                e.setSemester(d.getSemester());
                if (d.getSubjects() != null) e.setSubjects(new ArrayList<>(d.getSubjects()));
                e.setCountry(entity);
                entity.getSyllabusSemesters().add(e);
            });
        }

        entity.getFmgePassRates().clear();
        if (dto.getFmgePassRates() != null) {
            dto.getFmgePassRates().forEach(d -> {
                CountryFmgePassRate e = new CountryFmgePassRate();
                e.setUniversity(d.getUniversity());
                e.setPassRate(d.getPassRate());
                e.setCountry(entity);
                entity.getFmgePassRates().add(e);
            });
        }

        entity.getFeeStructureRows().clear();
        if (dto.getFeeStructureRows() != null) {
            dto.getFeeStructureRows().forEach(d -> {
                CountryFeeStructure e = new CountryFeeStructure();
                e.setUniversityName(d.getUniversityName());
                e.setTuitionFeeNative(d.getTuitionFeeNative());
                e.setHostelFeeNative(d.getHostelFeeNative());
                e.setNativeCurrencyCode(d.getNativeCurrencyCode());
                e.setAnnualTuitionUsd(d.getAnnualTuitionUsd());
                e.setAnnualHostelUsd(d.getAnnualHostelUsd());
                e.setCountry(entity);
                entity.getFeeStructureRows().add(e);
            });
        }

        entity.getLivingCostRows().clear();
        if (dto.getLivingCostRows() != null) {
            dto.getLivingCostRows().forEach(d -> {
                CountryLivingCost e = new CountryLivingCost();
                e.setExpenseCategory(d.getExpenseCategory());
                e.setMonthlyCost(d.getMonthlyCost());
                e.setCountry(entity);
                entity.getLivingCostRows().add(e);
            });
        }

        entity.getScholarshipTypes().clear();
        if (dto.getScholarshipTypes() != null) {
            dto.getScholarshipTypes().forEach(d -> {
                CountryScholarship e = new CountryScholarship();
                e.setName(d.getName());
                e.setDescription(d.getDescription());
                e.setCountry(entity);
                entity.getScholarshipTypes().add(e);
            });
        }

        entity.getHostelFacilities().clear();
        if (dto.getHostelFacilities() != null) {
            dto.getHostelFacilities().forEach(d -> {
                CountryHostelFacility e = new CountryHostelFacility();
                e.setCriteria(d.getCriteria());
                e.setInformation(d.getInformation());
                e.setCountry(entity);
                entity.getHostelFacilities().add(e);
            });
        }

        entity.getAboutCountryTable().clear();
        if (dto.getAboutCountryTable() != null) {
            dto.getAboutCountryTable().forEach(d -> {
                CountryAboutAttribute e = new CountryAboutAttribute();
                e.setAttribute(d.getAttribute());
                e.setDetail(d.getDetail());
                e.setCountry(entity);
                entity.getAboutCountryTable().add(e);
            });
        }

        entity.getPopularDestinations().clear();
        if (dto.getPopularDestinations() != null) {
            dto.getPopularDestinations().forEach(d -> {
                CountryDestination e = new CountryDestination();
                e.setName(d.getName());
                e.setSlug(d.getSlug());
                e.setFlag(d.getFlag());
                e.setCountry(entity);
                entity.getPopularDestinations().add(e);
            });
        }

        entity.getFaqs().clear();
        if (dto.getFaqs() != null) {
            dto.getFaqs().forEach(d -> {
                CountryFaq e = new CountryFaq();
                e.setQuestion(d.getQuestion());
                e.setAnswer(d.getAnswer());
                e.setCountry(entity);
                entity.getFaqs().add(e);
            });
        }
    }
}