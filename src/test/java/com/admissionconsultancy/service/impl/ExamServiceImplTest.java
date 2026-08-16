package com.admissionconsultancy.service.impl;

import com.admissionconsultancy.dto.*;
import com.admissionconsultancy.entity.*;
import com.admissionconsultancy.exception.DuplicateResourceException;
import com.admissionconsultancy.exception.ResourceNotFoundException;
import com.admissionconsultancy.mapper.ExamMapper;
import com.admissionconsultancy.repository.*;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamServiceImplTest {

    @Mock
    private ExamRepository examRepository;

    @Mock
    private ExamCountryRepository examCountryRepository;

    @Mock
    private ExamRequirementRepository requirementRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private UniversityRepository universityRepository;

    @Mock
    private CollegeRepository collegeRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ExamMapper examMapper;

    private MeterRegistry meterRegistry;
    private ExamServiceImpl examService;

    private Exam examEntity;
    private ExamDTO examDTO;
    private Country countryEntity;
    private ExamRequirement requirementEntity;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        examService = new ExamServiceImpl(
                examRepository,
                examCountryRepository,
                requirementRepository,
                countryRepository,
                universityRepository,
                collegeRepository,
                courseRepository,
                examMapper,
                meterRegistry
        );

        examEntity = new Exam();
        examEntity.setName("IELTS");
        examEntity.setCode("IELTS");
        examEntity.setDescription("English proficiency");
        examEntity.setActive(true);

        examDTO = new ExamDTO();
        examDTO.setName("IELTS");
        examDTO.setCode("IELTS");
        examDTO.setDescription("English proficiency");
        examDTO.setActive(true);

        countryEntity = new Country();
        countryEntity.setName("United Kingdom");
        countryEntity.setSlug("uk");

        requirementEntity = new ExamRequirement();
        requirementEntity.setExam(examEntity);
        requirementEntity.setCountry(countryEntity);
        requirementEntity.setRequired(true);
        requirementEntity.setMinimumScore(6.5);
    }

    @Test
    @DisplayName("Should fetch active exams successfully and record timer")
    void testGetActiveExams() {
        when(examRepository.findAllByIsActiveTrue()).thenReturn(List.of(examEntity));
        when(examMapper.toDTO(examEntity)).thenReturn(examDTO);

        List<ExamDTO> result = examService.getActiveExams();

        assertThat(result).isNotNull().hasSize(1);
        verify(examRepository, times(1)).findAllByIsActiveTrue();
        assertThat(meterRegistry.get("db.query.exam.fetch.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should fetch all exams for admin and record timer")
    void testGetAllExamsAdmin() {
        when(examRepository.findAll()).thenReturn(List.of(examEntity));
        when(examMapper.toDTO(examEntity)).thenReturn(examDTO);

        List<ExamDTO> result = examService.getAllExamsAdmin();

        assertThat(result).isNotNull().hasSize(1);
        verify(examRepository, times(1)).findAll();
        assertThat(meterRegistry.get("db.query.exam.fetch.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should fetch exam by code successfully and record timer")
    void testGetExamByCode_Success() {
        when(examRepository.findByCodeAndIsActiveTrue("IELTS")).thenReturn(Optional.of(examEntity));
        when(examMapper.toDTO(examEntity)).thenReturn(examDTO);

        ExamDTO result = examService.getExamByCode("IELTS");

        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("IELTS");
        verify(examRepository, times(1)).findByCodeAndIsActiveTrue("IELTS");
        assertThat(meterRegistry.get("db.query.exam.fetch.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when exam code is not found")
    void testGetExamByCode_NotFound() {
        when(examRepository.findByCodeAndIsActiveTrue("INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> examService.getExamByCode("INVALID"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should create new exam successfully, increment counter, and record timer")
    void testCreateExam_Success() {
        when(examRepository.existsByCode("IELTS")).thenReturn(false);
        when(examRepository.save(any(Exam.class))).thenReturn(examEntity);
        when(examMapper.toDTO(examEntity)).thenReturn(examDTO);

        ExamDTO result = examService.createExam(examDTO);

        assertThat(result).isNotNull();
        verify(examRepository, times(1)).existsByCode("IELTS");
        verify(examRepository, times(1)).save(any(Exam.class));

        assertThat(meterRegistry.get("business.exam.created.total").counter().count()).isEqualTo(1.0);
        assertThat(meterRegistry.get("db.query.exam.save.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when creating exam with duplicate code")
    void testCreateExam_DuplicateCode() {
        when(examRepository.existsByCode("IELTS")).thenReturn(true);

        assertThatThrownBy(() -> examService.createExam(examDTO))
                .isInstanceOf(DuplicateResourceException.class);

        verify(examRepository, times(1)).existsByCode("IELTS");
        verify(examRepository, never()).save(any(Exam.class));
    }

    @Test
    @DisplayName("Should update exam successfully, increment counter, and record timer")
    void testUpdateExam_Success() {
        when(examRepository.findByCode("IELTS")).thenReturn(Optional.of(examEntity));
        when(examRepository.save(any(Exam.class))).thenReturn(examEntity);
        when(examMapper.toDTO(examEntity)).thenReturn(examDTO);

        ExamDTO result = examService.updateExam("IELTS", examDTO);

        assertThat(result).isNotNull();
        verify(examRepository, times(1)).findByCode("IELTS");
        verify(examRepository, times(1)).save(any(Exam.class));

        assertThat(meterRegistry.get("business.exam.updated.total").counter().count()).isEqualTo(1.0);
        assertThat(meterRegistry.get("db.query.exam.save.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent exam")
    void testUpdateExam_NotFound() {
        when(examRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> examService.updateExam("INVALID", examDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should soft delete exam successfully, increment counter, and record timer")
    void testDeleteExam_Success() {
        when(examRepository.findByCode("IELTS")).thenReturn(Optional.of(examEntity));
        when(examRepository.save(any(Exam.class))).thenReturn(examEntity);

        examService.deleteExam("IELTS");

        ArgumentCaptor<Exam> captor = ArgumentCaptor.forClass(Exam.class);
        verify(examRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().isActive()).isFalse();

        assertThat(meterRegistry.get("business.exam.deleted.total").counter().count()).isEqualTo(1.0);
        assertThat(meterRegistry.get("db.query.exam.save.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent exam")
    void testDeleteExam_NotFound() {
        when(examRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> examService.deleteExam("INVALID"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should map exam code to country slug successfully")
    void testMapExamToCountry_Success() {
        when(examRepository.findByCode("IELTS")).thenReturn(Optional.of(examEntity));
        when(countryRepository.findBySlug("uk")).thenReturn(Optional.of(countryEntity));
        when(examCountryRepository.existsByExamCodeAndCountrySlug("IELTS", "uk")).thenReturn(false);
        when(examCountryRepository.save(any(ExamCountry.class))).thenReturn(new ExamCountry());

        examService.mapExamToCountry("IELTS", "uk");

        verify(examCountryRepository, times(1)).save(any(ExamCountry.class));
    }

    @Test
    @DisplayName("Should add exam requirement successfully with optional university, college, and course slugs")
    void testAddExamRequirement_Success() {
        ExamRequirementDTO reqDTO = new ExamRequirementDTO();
        reqDTO.setExamCode("IELTS");
        reqDTO.setCountrySlug("uk");
        reqDTO.setRequired(true);
        reqDTO.setMinimumScore(7.0);
        reqDTO.setUniversitySlug("oxford");
        reqDTO.setCollegeSlug("st-george");
        reqDTO.setCourseSlug("mbbs");

        University uni = new University();
        uni.setCountry(countryEntity);

        College col = new College();
        col.setCountry(countryEntity);

        when(examCountryRepository.existsByExamCodeAndCountrySlugAndIsActiveTrue("IELTS", "uk")).thenReturn(true);
        when(examRepository.findByCode("IELTS")).thenReturn(Optional.of(examEntity));
        when(countryRepository.findBySlug("uk")).thenReturn(Optional.of(countryEntity));
        when(universityRepository.findBySlug("oxford")).thenReturn(Optional.of(uni));
        when(collegeRepository.findBySlug("st-george")).thenReturn(Optional.of(col));
        when(courseRepository.findBySlug("mbbs")).thenReturn(Optional.of(new Course()));
        when(requirementRepository.save(any(ExamRequirement.class))).thenReturn(requirementEntity);

        examService.addExamRequirement(reqDTO);

        verify(requirementRepository, times(1)).save(any(ExamRequirement.class));
        assertThat(meterRegistry.get("business.exam_requirement.added.total").counter().count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when adding requirement for unmapped country slug")
    void testAddExamRequirement_NotMapped() {
        ExamRequirementDTO reqDTO = new ExamRequirementDTO();
        reqDTO.setExamCode("IELTS");
        reqDTO.setCountrySlug("uk");

        when(examCountryRepository.existsByExamCodeAndCountrySlugAndIsActiveTrue("IELTS", "uk")).thenReturn(false);

        assertThatThrownBy(() -> examService.addExamRequirement(reqDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Exam is not mapped");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when university country mismatches requirement country slug")
    void testAddExamRequirement_UniversityCountryMismatch() {
        ExamRequirementDTO reqDTO = new ExamRequirementDTO();
        reqDTO.setExamCode("IELTS");
        reqDTO.setCountrySlug("uk");
        reqDTO.setUniversitySlug("oxford");

        Country otherCountry = new Country();
        otherCountry.setSlug("other-country");

        University uni = new University();
        uni.setCountry(otherCountry);

        when(examCountryRepository.existsByExamCodeAndCountrySlugAndIsActiveTrue("IELTS", "uk")).thenReturn(true);
        when(examRepository.findByCode("IELTS")).thenReturn(Optional.of(examEntity));
        when(countryRepository.findBySlug("uk")).thenReturn(Optional.of(countryEntity));
        when(universityRepository.findBySlug("oxford")).thenReturn(Optional.of(uni));

        assertThatThrownBy(() -> examService.addExamRequirement(reqDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Country mismatch");
    }

    @Test
    @DisplayName("Should fetch country exam overview successfully and record timer")
    void testGetRequirementsByCountrySlug() {
        ExamCountry ec = new ExamCountry();
        ec.setExam(examEntity);

        when(countryRepository.findBySlugAndIsActiveTrue("uk")).thenReturn(Optional.of(countryEntity));
        when(examCountryRepository.findActiveExamsByCountrySlug("uk")).thenReturn(List.of(ec));
        when(examMapper.toDTO(examEntity)).thenReturn(examDTO);
        when(requirementRepository.findAllByCountrySlugWithDetails("uk")).thenReturn(List.of(requirementEntity));

        CountryExamOverviewDTO result = examService.getRequirementsByCountrySlug("uk");

        assertThat(result).isNotNull();
        assertThat(result.getCountryName()).isEqualTo("United Kingdom");
        assertThat(result.getApplicableExams()).hasSize(1);
        assertThat(result.getRequirements()).hasSize(1);
        assertThat(meterRegistry.get("db.query.exam_overview.by_country.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should fetch global exam overview successfully and record timer")
    void testGetRequirementsByExamCode() {
        ExamCountry ec = new ExamCountry();
        ec.setCountry(countryEntity);

        when(examRepository.findByCodeAndIsActiveTrue("IELTS")).thenReturn(Optional.of(examEntity));
        when(examCountryRepository.findActiveCountriesByExamCode("IELTS")).thenReturn(List.of(ec));
        when(requirementRepository.findAllByExamCodeWithDetails("IELTS")).thenReturn(List.of(requirementEntity));

        ExamGlobalOverviewDTO result = examService.getRequirementsByExamCode("IELTS");

        assertThat(result).isNotNull();
        assertThat(result.getExamCode()).isEqualTo("IELTS");
        assertThat(result.getApplicableCountries()).hasSize(1);
        assertThat(result.getGlobalRequirements()).hasSize(1);
        assertThat(meterRegistry.get("db.query.exam_overview.by_exam.time").timer().count()).isEqualTo(1);
    }
}