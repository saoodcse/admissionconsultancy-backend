package com.admissionconsultancy.service.impl;

import com.admissionconsultancy.constants.AppCode;
import com.admissionconsultancy.dto.EducationContentDTO;
import com.admissionconsultancy.entity.EducationContent;
import com.admissionconsultancy.exception.DuplicateResourceException;
import com.admissionconsultancy.exception.ResourceNotFoundException;
import com.admissionconsultancy.mapper.EducationContentMapper;
import com.admissionconsultancy.repository.*;
import com.admissionconsultancy.service.EducationContentService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EducationContentServiceImpl implements EducationContentService {

    private final EducationContentRepository contentRepository;
    private final CountryRepository countryRepository;
    private final UniversityRepository universityRepository;
    private final CourseRepository courseRepository;
    private final EducationContentMapper contentMapper;

    // Metrics Counters & Timers
    private final Counter contentCreatedCounter;
    private final Counter contentUpdatedCounter;
    private final Counter contentDeletedCounter;
    private final Timer fetchContentTimer;
    private final Timer saveContentTimer;

    public EducationContentServiceImpl(
            EducationContentRepository contentRepository,
            CountryRepository countryRepository,
            UniversityRepository universityRepository,
            CourseRepository courseRepository,
            EducationContentMapper contentMapper,
            MeterRegistry meterRegistry) {
        this.contentRepository = contentRepository;
        this.countryRepository = countryRepository;
        this.universityRepository = universityRepository;
        this.courseRepository = courseRepository;
        this.contentMapper = contentMapper;

        this.contentCreatedCounter = meterRegistry.counter("business.content.created.total");
        this.contentUpdatedCounter = meterRegistry.counter("business.content.updated.total");
        this.contentDeletedCounter = meterRegistry.counter("business.content.deleted.total");

        this.fetchContentTimer = meterRegistry.timer("db.query.content.fetch.time");
        this.saveContentTimer = meterRegistry.timer("db.query.content.save.time");
    }

    @Override
    public List<EducationContentDTO> getActiveContents() {
        log.info("Fetching all active education contents");
        return fetchContentTimer.record(() -> contentRepository.findAllByActiveTrue())
                .stream().map(contentMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<EducationContentDTO> getContentsByCategory(String category) {
        log.info("Fetching contents for category: '{}'", category);
        return fetchContentTimer.record(() -> contentRepository.findByCategoryAndActiveTrue(category))
                .stream().map(contentMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<EducationContentDTO> getContentsByCountry(String countrySlug) {
        log.info("Fetching contents tagged to country slug: '{}'", countrySlug);
        return fetchContentTimer.record(() -> contentRepository.findByTaggedCountrySlugAndActiveTrue(countrySlug))
                .stream().map(contentMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<EducationContentDTO> getContentsByUniversity(String universitySlug) {
        log.info("Fetching contents tagged to university slug: '{}'", universitySlug);
        return fetchContentTimer.record(() -> contentRepository.findByTaggedUniversitySlugAndActiveTrue(universitySlug))
                .stream().map(contentMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<EducationContentDTO> getContentsByCourse(String courseSlug) {
        log.info("Fetching contents tagged to course slug: '{}'", courseSlug);
        return fetchContentTimer.record(() -> contentRepository.findByTaggedCourseSlugAndActiveTrue(courseSlug))
                .stream().map(contentMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public EducationContentDTO getContentBySlug(String slug) {
        log.info("Fetching content by slug: '{}'", slug);
        EducationContent content = fetchContentTimer.record(() -> contentRepository.findByUrlSlugAndActiveTrue(slug))
                .orElseThrow(() -> new ResourceNotFoundException("Content not found for slug: " + slug));
        return contentMapper.toDTO(content);
    }

    @Override
    @Transactional
    public EducationContentDTO createContent(EducationContentDTO contentDTO) {
        log.info("Creating content with slug: '{}'", contentDTO.getUrlSlug());

        // Fixed: Check active records only so soft-deleted items don't block recreation
        if (contentRepository.existsByUrlSlugAndActiveTrue(contentDTO.getUrlSlug())) {
            throw new DuplicateResourceException(AppCode.BAD_REQUEST);
        }

        EducationContent entity = new EducationContent();
        contentMapper.updateEntityFromDTO(contentDTO, entity);
        entity.setActive(true);

        resolveAndSetTags(contentDTO, entity);

        EducationContent saved = saveContentTimer.record(() -> contentRepository.save(entity));
        contentCreatedCounter.increment();
        return contentMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public EducationContentDTO updateContent(String slug, EducationContentDTO contentDTO) {
        log.info("Updating content with slug: '{}'", slug);
        EducationContent existing = contentRepository.findByUrlSlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found for slug: " + slug));

        contentMapper.updateEntityFromDTO(contentDTO, existing);
        resolveAndSetTags(contentDTO, existing);

        EducationContent saved = saveContentTimer.record(() -> contentRepository.save(existing));
        contentUpdatedCounter.increment();
        return contentMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public void deleteContent(String slug) {
        log.info("Permanently deleting content with slug: '{}'", slug);

        EducationContent existing = contentRepository.findByUrlSlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found for slug: " + slug));

        // Hard delete removes the row entirely from the database
        contentRepository.delete(existing);

        contentDeletedCounter.increment();
    }

    private void resolveAndSetTags(EducationContentDTO dto, EducationContent entity) {
        if (dto.getCountry() != null && dto.getCountry().getSlug() != null) {
            countryRepository.findBySlug(dto.getCountry().getSlug())
                    .ifPresent(entity::setTaggedCountry);
        } else {
            entity.setTaggedCountry(null);
        }

        if (dto.getUniversity() != null && dto.getUniversity().getSlug() != null) {
            universityRepository.findBySlug(dto.getUniversity().getSlug())
                    .ifPresent(entity::setTaggedUniversity);
        } else {
            entity.setTaggedUniversity(null);
        }

        if (dto.getCourse() != null && dto.getCourse().getSlug() != null) {
            courseRepository.findBySlug(dto.getCourse().getSlug())
                    .ifPresent(entity::setTaggedCourse);
        } else {
            entity.setTaggedCourse(null);
        }
    }
}