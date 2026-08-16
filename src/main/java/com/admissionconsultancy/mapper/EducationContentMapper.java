package com.admissionconsultancy.mapper;

import com.admissionconsultancy.dto.CountrySummaryDTO;
import com.admissionconsultancy.dto.CourseSummaryDTO;
import com.admissionconsultancy.dto.EducationContentDTO;
import com.admissionconsultancy.dto.UniversitySummaryDTO;
import com.admissionconsultancy.entity.EducationContent;
import org.springframework.stereotype.Component;

@Component
public class EducationContentMapper {

    public EducationContentDTO toDTO(EducationContent entity) {
        if (entity == null) return null;

        EducationContentDTO dto = new EducationContentDTO();
        dto.setId(entity.getId());
        dto.setContentType(entity.getContentType());
        dto.setSeoTitle(entity.getSeoTitle());
        dto.setMetaTitle(entity.getMetaTitle());
        dto.setMetaDescription(entity.getMetaDescription());
        dto.setUrlSlug(entity.getUrlSlug());
        dto.setPrimaryKeyword(entity.getPrimaryKeyword());
        dto.setSecondaryKeywords(entity.getSecondaryKeywords());
        dto.setArticleContent(entity.getArticleContent());
        dto.setFaqs(entity.getFaqs());
        dto.setCategory(entity.getCategory());
        dto.setActive(entity.isActive());

        // Map Country Tag
        if (entity.getTaggedCountry() != null) {
            CountrySummaryDTO cDto = new CountrySummaryDTO();
            cDto.setName(entity.getTaggedCountry().getName());
            cDto.setSlug(entity.getTaggedCountry().getSlug());
            cDto.setFlag(entity.getTaggedCountry().getFlag());
            dto.setCountry(cDto);
        }

        // Map University Tag
        if (entity.getTaggedUniversity() != null) {
            UniversitySummaryDTO uDto = new UniversitySummaryDTO();
            uDto.setName(entity.getTaggedUniversity().getName());
            uDto.setSlug(entity.getTaggedUniversity().getSlug());
            uDto.setLogo(entity.getTaggedUniversity().getLogo());
            if (entity.getTaggedUniversity().getCountry() != null) {
                uDto.setCountryName(entity.getTaggedUniversity().getCountry().getName());
            }
            dto.setUniversity(uDto);
        }

        // Map Course Tag
        if (entity.getTaggedCourse() != null) {
            CourseSummaryDTO coDto = new CourseSummaryDTO();
            coDto.setName(entity.getTaggedCourse().getName());
            coDto.setSlug(entity.getTaggedCourse().getSlug());
            coDto.setDegreeType(entity.getTaggedCourse().getDegreeType());
            dto.setCourse(coDto);
        }

        return dto;
    }

    public void updateEntityFromDTO(EducationContentDTO dto, EducationContent entity) {
        if (dto == null || entity == null) return;

        if (dto.getContentType() != null) entity.setContentType(dto.getContentType());
        if (dto.getSeoTitle() != null) entity.setSeoTitle(dto.getSeoTitle());
        if (dto.getMetaTitle() != null) entity.setMetaTitle(dto.getMetaTitle());
        if (dto.getMetaDescription() != null) entity.setMetaDescription(dto.getMetaDescription());
        if (dto.getUrlSlug() != null) entity.setUrlSlug(dto.getUrlSlug());
        if (dto.getPrimaryKeyword() != null) entity.setPrimaryKeyword(dto.getPrimaryKeyword());
        if (dto.getSecondaryKeywords() != null) entity.setSecondaryKeywords(dto.getSecondaryKeywords());
        if (dto.getArticleContent() != null) entity.setArticleContent(dto.getArticleContent());
        if (dto.getFaqs() != null) entity.setFaqs(dto.getFaqs());
        if (dto.getCategory() != null) entity.setCategory(dto.getCategory());
    }
}