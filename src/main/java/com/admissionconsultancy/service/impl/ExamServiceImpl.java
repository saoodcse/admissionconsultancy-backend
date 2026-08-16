package com.admissionconsultancy.service.impl;

import com.admissionconsultancy.constants.AppCode;
import com.admissionconsultancy.dto.*;
import com.admissionconsultancy.entity.*;
import com.admissionconsultancy.exception.DuplicateResourceException;
import com.admissionconsultancy.exception.ResourceNotFoundException;
import com.admissionconsultancy.mapper.ExamMapper;
import com.admissionconsultancy.repository.*;
import com.admissionconsultancy.service.ExamService;
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
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final ExamCountryRepository examCountryRepository;
    private final ExamRequirementRepository requirementRepository;
    private final CountryRepository countryRepository;
    private final UniversityRepository universityRepository;
    private final CollegeRepository collegeRepository;
    private final CourseRepository courseRepository;
    private final ExamMapper examMapper;

    // Micrometer Metrics
    private final Counter examCreatedCounter;
    private final Counter examUpdatedCounter;
    private final Counter examDeletedCounter;
    private final Counter requirementAddedCounter;
    private final Timer fetchExamsTimer;
    private final Timer saveExamTimer;
    private final Timer fetchCountryOverviewTimer;
    private final Timer fetchExamGlobalOverviewTimer;

    public ExamServiceImpl(
            ExamRepository examRepository,
            ExamCountryRepository examCountryRepository,
            ExamRequirementRepository requirementRepository,
            CountryRepository countryRepository,
            UniversityRepository universityRepository,
            CollegeRepository collegeRepository,
            CourseRepository courseRepository,
            ExamMapper examMapper,
            MeterRegistry meterRegistry ) {

        this.examRepository = examRepository;
        this.examCountryRepository = examCountryRepository;
        this.requirementRepository = requirementRepository;
        this.countryRepository = countryRepository;
        this.universityRepository = universityRepository;
        this.collegeRepository = collegeRepository;
        this.courseRepository = courseRepository;
        this.examMapper = examMapper;

        this.examCreatedCounter = meterRegistry.counter("business.exam.created.total");
        this.examUpdatedCounter = meterRegistry.counter("business.exam.updated.total");
        this.examDeletedCounter = meterRegistry.counter("business.exam.deleted.total");
        this.requirementAddedCounter = meterRegistry.counter("business.exam_requirement.added.total");

        this.fetchExamsTimer = meterRegistry.timer("db.query.exam.fetch.time");
        this.saveExamTimer = meterRegistry.timer("db.query.exam.save.time");
        this.fetchCountryOverviewTimer = meterRegistry.timer("db.query.exam_overview.by_country.time");
        this.fetchExamGlobalOverviewTimer = meterRegistry.timer("db.query.exam_overview.by_exam.time");
    }

    @Override
    public List<ExamDTO> getActiveExams() {
        log.info("Fetching all active exams");
        return fetchExamsTimer.record(() -> examRepository.findAllByIsActiveTrue())
                .stream()
                .map(examMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExamDTO> getAllExamsAdmin() {
        log.info("Admin: Fetching all exams");
        return fetchExamsTimer.record(()-> examRepository.findAll())
                .stream()
                .map(examMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ExamDTO getExamByCode(String code) {
        log.info("Fetching exam by code: '{}'", code);
        Exam exam = fetchExamsTimer.record(() -> examRepository.findByCodeAndIsActiveTrue(code))
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found for code: " + code));
        return examMapper.toDTO(exam);
    }

    @Override
    @Transactional
    public ExamDTO createExam(ExamDTO examDTO) {
        log.info("Creating new master exam: '{}'", examDTO.getCode());

        if (examRepository.existsByCode(examDTO.getCode())) {
            throw new DuplicateResourceException(AppCode.BAD_REQUEST);
        }

        Exam exam = new Exam();
        examMapper.updateEntityFromDTO(examDTO, exam);
        exam.setActive(true);

        Exam savedExam = saveExamTimer.record(() -> examRepository.save(exam));
        examCreatedCounter.increment();

        return examMapper.toDTO(savedExam);
    }

    @Override
    @Transactional
    public ExamDTO updateExam(String code, ExamDTO examDTO) {
        log.info("Updating master exam with code: {}", code);

        Exam existingExam = examRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        examMapper.updateEntityFromDTO(examDTO, existingExam);

        Exam savedExam = saveExamTimer.record(() -> examRepository.save(existingExam));
        examUpdatedCounter.increment();

        return examMapper.toDTO(savedExam);
    }

    @Override
    @Transactional
    public void deleteExam(String code) {
        log.info("Soft deleting exam with code: {}", code);

        Exam exam = examRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        exam.setActive(false);
        saveExamTimer.record(() -> examRepository.save(exam));

        examDeletedCounter.increment();
    }

    @Override
    @Transactional
    public void mapExamToCountry(String examCode, String countrySlug) {
        Exam exam = examRepository.findByCode(examCode)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        Country country = countryRepository.findBySlug(countrySlug)
                .orElseThrow(() -> new ResourceNotFoundException("Country not found"));

        if (!examCountryRepository.existsByExamCodeAndCountrySlug(examCode, countrySlug)) {
            ExamCountry examCountry = new ExamCountry();
            examCountry.setExam(exam);
            examCountry.setCountry(country);
            examCountry.setActive(true);
            saveExamTimer.record(() -> examCountryRepository.save(examCountry));
        }
    }

    @Override
    @Transactional
    public void addExamRequirement(ExamRequirementDTO reqDTO) {
        if (!examCountryRepository.existsByExamCodeAndCountrySlugAndIsActiveTrue(reqDTO.getExamCode(), reqDTO.getCountrySlug())) {
            throw new IllegalArgumentException("Exam is not mapped as applicable for this country.");
        }

        Exam exam = examRepository.findByCode(reqDTO.getExamCode()).orElseThrow();
        Country country = countryRepository.findBySlug(reqDTO.getCountrySlug()).orElseThrow();

        ExamRequirement requirement = new ExamRequirement();
        requirement.setExam(exam);
        requirement.setCountry(country);
        requirement.setRequired(reqDTO.isRequired());
        requirement.setMinimumScore(reqDTO.getMinimumScore());
        requirement.setMinimumPercentile(reqDTO.getMinimumPercentile());

        if (reqDTO.getUniversitySlug() != null) {
            University uni = universityRepository.findBySlug(reqDTO.getUniversitySlug()).orElseThrow();
            if (!uni.getCountry().getSlug().equals(country.getSlug())) throw new IllegalArgumentException("Country mismatch");
            requirement.setUniversity(uni);
        }
        if (reqDTO.getCollegeSlug() != null) {
            College col = collegeRepository.findBySlug(reqDTO.getCollegeSlug()).orElseThrow();
            if (!col.getCountry().getSlug().equals(country.getSlug())) throw new IllegalArgumentException("Country mismatch");
            requirement.setCollege(col);
        }
        if (reqDTO.getCourseSlug() != null) {
            requirement.setCourse(courseRepository.findBySlug(reqDTO.getCourseSlug()).orElseThrow());
        }

        requirementRepository.save(requirement);
        requirementAddedCounter.increment();
    }

    @Override
    @Transactional(readOnly = true)
    public CountryExamOverviewDTO getRequirementsByCountrySlug(String countrySlug) {
        log.info("Fetching exam overview for country slug: {}", countrySlug);

        return fetchCountryOverviewTimer.record(() -> {
            Country country = countryRepository.findBySlugAndIsActiveTrue(countrySlug)
                    .orElseThrow(() -> new ResourceNotFoundException("Country not found"));

            CountryExamOverviewDTO response = new CountryExamOverviewDTO();
            response.setCountryName(country.getName());
            response.setCountrySlug(country.getSlug());

            // 1. Fetch Applicable Exams for this Country
            List<ExamDTO> applicableExams = examCountryRepository.findActiveExamsByCountrySlug(country.getSlug())
                    .stream()
                    .map(ec -> examMapper.toDTO(ec.getExam()))
                    .collect(Collectors.toList());
            response.setApplicableExams(applicableExams);

            // 2. Fetch specific requirements with joined tables
            List<RequirementDetailDTO> reqDTOs = requirementRepository.findAllByCountrySlugWithDetails(country.getSlug())
                    .stream()
                    .map(this::mapToRequirementDetail)
                    .collect(Collectors.toList());
            response.setRequirements(reqDTOs);

            return response;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public ExamGlobalOverviewDTO getRequirementsByExamCode(String examCode) {
        log.info("Fetching global overview for exam code: {}", examCode);

        return fetchExamGlobalOverviewTimer.record(() -> {
            Exam exam = examRepository.findByCodeAndIsActiveTrue(examCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

            ExamGlobalOverviewDTO response = new ExamGlobalOverviewDTO();
            response.setExamName(exam.getName());
            response.setExamCode(exam.getCode());
            response.setDescription(exam.getDescription());

            // Map SEO & Administrative Metadata to Global Overview DTO
            response.setMetaTitle(exam.getMetaTitle());
            response.setMetaDescription(exam.getMetaDescription());

            // 1. Fetch Countries where this exam is active
            List<String> applicableCountries = examCountryRepository.findActiveCountriesByExamCode(exam.getCode())
                    .stream()
                    .map(ec -> ec.getCountry().getName())
                    .collect(Collectors.toList());
            response.setApplicableCountries(applicableCountries);

            // 2. Fetch specific global requirements
            List<RequirementDetailDTO> reqDTOs = requirementRepository.findAllByExamCodeWithDetails(exam.getCode())
                    .stream()
                    .map(this::mapToRequirementDetail)
                    .collect(Collectors.toList());
            response.setGlobalRequirements(reqDTOs);

            return response;
        });
    }

    /**
     * Helper method to map the highly nested ExamRequirement entity to a flat, UI-friendly DTO.
     */
    private RequirementDetailDTO mapToRequirementDetail(ExamRequirement er) {
        RequirementDetailDTO dto = new RequirementDetailDTO();

        dto.setExamName(er.getExam().getName());
        dto.setExamCode(er.getExam().getCode());
        dto.setCountryName(er.getCountry().getName());

        if (er.getUniversity() != null) dto.setUniversityName(er.getUniversity().getName());
        if (er.getCollege() != null) dto.setCollegeName(er.getCollege().getName());
        if (er.getCourse() != null) dto.setCourseName(er.getCourse().getName());

        dto.setRequired(er.isRequired());
        dto.setMinimumScore(er.getMinimumScore());
        dto.setMinimumPercentile(er.getMinimumPercentile());

        return dto;
    }
}