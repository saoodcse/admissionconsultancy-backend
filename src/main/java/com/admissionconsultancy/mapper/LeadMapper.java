package com.admissionconsultancy.mapper;

import com.admissionconsultancy.dto.CollegeSummaryDTO;
import com.admissionconsultancy.dto.CountrySummaryDTO;
import com.admissionconsultancy.dto.CourseSummaryDTO;
import com.admissionconsultancy.dto.LeadDTO;
import com.admissionconsultancy.dto.UniversitySummaryDTO;
import com.admissionconsultancy.entity.Lead;
import org.springframework.stereotype.Component;

@Component
public class LeadMapper {

    public LeadDTO toDTO(Lead entity) {
        if (entity == null) return null;

        LeadDTO dto = new LeadDTO();
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setCity(entity.getCity());
        dto.setMessage(entity.getMessage());
        dto.setSource(entity.getSource());
        dto.setStatus(entity.getStatus());
        dto.setAdminNotes(entity.getAdminNotes());

        // --- Admissions & Intent Tracking ---
        dto.setTargetIntake(entity.getTargetIntake());
        dto.setPreferredBudgetUsd(entity.getPreferredBudgetUsd());

        // --- Marketing Attribution (UTM Parameters) ---
        dto.setUtmSource(entity.getUtmSource());
        dto.setUtmMedium(entity.getUtmMedium());
        dto.setUtmCampaign(entity.getUtmCampaign());

        // --- Assignment & Follow-up ---
        dto.setAssignedCounselorEmail(entity.getAssignedCounselorEmail());

        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // Map Country Summary
        if (entity.getInterestedCountry() != null) {
            CountrySummaryDTO countryDTO = new CountrySummaryDTO();
            countryDTO.setName(entity.getInterestedCountry().getName());
            countryDTO.setSlug(entity.getInterestedCountry().getSlug());
            countryDTO.setFlag(entity.getInterestedCountry().getFlag());
            dto.setCountry(countryDTO);
        }

        // Map University Summary
        if (entity.getInterestedUniversity() != null) {
            UniversitySummaryDTO uniDTO = new UniversitySummaryDTO();
            uniDTO.setName(entity.getInterestedUniversity().getName());
            uniDTO.setSlug(entity.getInterestedUniversity().getSlug());
            uniDTO.setLogo(entity.getInterestedUniversity().getLogo());
            if (entity.getInterestedUniversity().getCountry() != null) {
                uniDTO.setCountryName(entity.getInterestedUniversity().getCountry().getName());
            }
            dto.setUniversity(uniDTO);
        }

        // Map College Summary
        if (entity.getInterestedCollege() != null) {
            CollegeSummaryDTO collegeDTO = new CollegeSummaryDTO();
            collegeDTO.setName(entity.getInterestedCollege().getName());
            collegeDTO.setSlug(entity.getInterestedCollege().getSlug());
            collegeDTO.setLogo(entity.getInterestedCollege().getLogo());
            if (entity.getInterestedCollege().getCountry() != null) {
                collegeDTO.setCountryName(entity.getInterestedCollege().getCountry().getName());
            }
            dto.setCollege(collegeDTO);
        }

        // Map Course Summary
        if (entity.getInterestedCourse() != null) {
            CourseSummaryDTO courseDTO = new CourseSummaryDTO();
            courseDTO.setName(entity.getInterestedCourse().getName());
            courseDTO.setSlug(entity.getInterestedCourse().getSlug());
            courseDTO.setDegreeType(entity.getInterestedCourse().getDegreeType());
            dto.setCourse(courseDTO);
        }

        return dto;
    }

    public void updateEntityFromDTO(LeadDTO dto, Lead entity) {
        if (dto == null || entity == null) return;

        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setCity(dto.getCity());
        entity.setMessage(dto.getMessage());

        if (dto.getSource() != null) entity.setSource(dto.getSource());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
        if (dto.getAdminNotes() != null) entity.setAdminNotes(dto.getAdminNotes());

        // --- Admissions & Intent Tracking ---
        if (dto.getTargetIntake() != null) entity.setTargetIntake(dto.getTargetIntake());
        if (dto.getPreferredBudgetUsd() != null) entity.setPreferredBudgetUsd(dto.getPreferredBudgetUsd());

        // --- Marketing Attribution (UTM Parameters) ---
        if (dto.getUtmSource() != null) entity.setUtmSource(dto.getUtmSource());
        if (dto.getUtmMedium() != null) entity.setUtmMedium(dto.getUtmMedium());
        if (dto.getUtmCampaign() != null) entity.setUtmCampaign(dto.getUtmCampaign());

        // --- Assignment & Follow-up ---
        if (dto.getAssignedCounselorEmail() != null) entity.setAssignedCounselorEmail(dto.getAssignedCounselorEmail());

        // Database entity linking (Country, University, College, Course) happens in the Service implementation
    }
}