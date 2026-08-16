package com.admissionconsultancy.service.impl;

import com.admissionconsultancy.dto.CollegeDTO;
import com.admissionconsultancy.dto.CountrySummaryDTO;
import com.admissionconsultancy.dto.CourseSummaryDTO;
import com.admissionconsultancy.dto.UniversitySummaryDTO;
import com.admissionconsultancy.entity.College;
import com.admissionconsultancy.entity.Country;
import com.admissionconsultancy.entity.Course;
import com.admissionconsultancy.entity.University;
import com.admissionconsultancy.exception.DuplicateResourceException;
import com.admissionconsultancy.exception.ResourceNotFoundException;
import com.admissionconsultancy.mapper.CollegeMapper;
import com.admissionconsultancy.repository.CollegeRepository;
import com.admissionconsultancy.repository.CountryRepository;
import com.admissionconsultancy.repository.CourseRepository;
import com.admissionconsultancy.repository.UniversityRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollegeServiceImplTest {

    @Mock
    private CollegeRepository collegeRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private UniversityRepository universityRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CollegeMapper collegeMapper;

    private MeterRegistry meterRegistry;
    private CollegeServiceImpl collegeService;

    private College collegeEntity;
    private CollegeDTO collegeDTO;
    private Country countryEntity;
    private University universityEntity;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        collegeService = new CollegeServiceImpl(
                collegeRepository,
                countryRepository,
                universityRepository,
                courseRepository,
                collegeMapper,
                meterRegistry
        );

        countryEntity = new Country();
        countryEntity.setName("United Kingdom");
        countryEntity.setSlug("uk");

        universityEntity = new University();
        universityEntity.setName("London Medical College");
        universityEntity.setSlug("london-medical");

        collegeEntity = new College();
        collegeEntity.setName("St. George College");
        collegeEntity.setSlug("st-george");
        collegeEntity.setActive(true);
        collegeEntity.setCountry(countryEntity);
        collegeEntity.setUniversity(universityEntity);
        collegeEntity.setOfferedCourses(new ArrayList<>());

        CountrySummaryDTO countrySummary = new CountrySummaryDTO();
        countrySummary.setSlug("uk");

        UniversitySummaryDTO universitySummary = new UniversitySummaryDTO();
        universitySummary.setSlug("london-medical");

        CourseSummaryDTO courseSummary = new CourseSummaryDTO();
        courseSummary.setSlug("mbbs");

        collegeDTO = new CollegeDTO();
        collegeDTO.setName("St. George College");
        collegeDTO.setSlug("st-george");
        collegeDTO.setCountry(countrySummary);
        collegeDTO.setUniversity(universitySummary);
        collegeDTO.setOfferedCourses(List.of(courseSummary));
    }

    @Test
    @DisplayName("Should fetch active colleges and record timer")
    void testGetActiveColleges() {
        when(collegeRepository.findAllByIsActiveTrue()).thenReturn(List.of(collegeEntity));
        when(collegeMapper.toDTO(collegeEntity)).thenReturn(collegeDTO);

        List<CollegeDTO> result = collegeService.getActiveColleges();

        assertThat(result).isNotNull().hasSize(1);
        verify(collegeRepository, times(1)).findAllByIsActiveTrue();
        assertThat(meterRegistry.get("db.query.college.fetch_active.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should fetch all colleges for admin and record timer")
    void testGetAllCollegesAdmin() {
        when(collegeRepository.findAll()).thenReturn(List.of(collegeEntity));
        when(collegeMapper.toDTO(collegeEntity)).thenReturn(collegeDTO);

        List<CollegeDTO> result = collegeService.getAllCollegesAdmin();

        assertThat(result).isNotNull().hasSize(1);
        verify(collegeRepository, times(1)).findAll();
        assertThat(meterRegistry.get("db.query.college.fetch_all_admin.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should fetch colleges by country slug and record timer")
    void testGetCollegesByCountry() {
        when(collegeRepository.findByCountrySlugAndIsActiveTrue("uk")).thenReturn(List.of(collegeEntity));
        when(collegeMapper.toDTO(collegeEntity)).thenReturn(collegeDTO);

        List<CollegeDTO> result = collegeService.getCollegesByCountry("uk");

        assertThat(result).isNotNull().hasSize(1);
        verify(collegeRepository, times(1)).findByCountrySlugAndIsActiveTrue("uk");
        assertThat(meterRegistry.get("db.query.college.fetch_by_country.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should fetch college by slug successfully and record timer")
    void testGetBySlug_Success() {
        when(collegeRepository.findBySlugAndIsActiveTrue("st-george")).thenReturn(Optional.of(collegeEntity));
        when(collegeMapper.toDTO(collegeEntity)).thenReturn(collegeDTO);

        CollegeDTO result = collegeService.getBySlug("st-george");

        assertThat(result).isNotNull();
        assertThat(result.getSlug()).isEqualTo("st-george");
        verify(collegeRepository, times(1)).findBySlugAndIsActiveTrue("st-george");
        assertThat(meterRegistry.get("db.query.college.fetch_by_slug.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when college slug is not found")
    void testGetBySlug_NotFound() {
        when(collegeRepository.findBySlugAndIsActiveTrue("invalid")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> collegeService.getBySlug("invalid"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(collegeRepository, times(1)).findBySlugAndIsActiveTrue("invalid");
    }

    @Test
    @DisplayName("Should create college successfully, increment counter, and record timer")
    void testCreateCollege_Success() {
        when(collegeRepository.existsBySlug("st-george")).thenReturn(false);
        when(countryRepository.findBySlug("uk")).thenReturn(Optional.of(countryEntity));
        when(universityRepository.findBySlug("london-medical")).thenReturn(Optional.of(universityEntity));
        when(courseRepository.findBySlugIn(any())).thenReturn(List.of(new Course()));
        when(collegeRepository.save(any(College.class))).thenReturn(collegeEntity);
        when(collegeMapper.toDTO(collegeEntity)).thenReturn(collegeDTO);

        CollegeDTO result = collegeService.createCollege(collegeDTO);

        assertThat(result).isNotNull();
        verify(collegeRepository, times(1)).existsBySlug("st-george");
        verify(countryRepository, times(1)).findBySlug("uk");
        verify(universityRepository, times(1)).findBySlug("london-medical");
        verify(collegeRepository, times(1)).save(any(College.class));

        assertThat(meterRegistry.get("business.college.created.total").counter().count()).isEqualTo(1.0);
        assertThat(meterRegistry.get("db.query.college.save.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when creating college with existing slug")
    void testCreateCollege_DuplicateSlug() {
        when(collegeRepository.existsBySlug("st-george")).thenReturn(true);

        assertThatThrownBy(() -> collegeService.createCollege(collegeDTO))
                .isInstanceOf(DuplicateResourceException.class);

        verify(collegeRepository, times(1)).existsBySlug("st-george");
        verify(collegeRepository, never()).save(any(College.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when country slug is missing on create")
    void testCreateCollege_MissingCountrySlug() {
        collegeDTO.setCountry(null);
        when(collegeRepository.existsBySlug("st-george")).thenReturn(false);

        assertThatThrownBy(() -> collegeService.createCollege(collegeDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("'country' with 'slug' is required");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when country slug does not exist on create")
    void testCreateCollege_CountryNotFound() {
        when(collegeRepository.existsBySlug("st-george")).thenReturn(false);
        when(countryRepository.findBySlug("uk")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> collegeService.createCollege(collegeDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when university slug does not exist on create")
    void testCreateCollege_UniversityNotFound() {
        when(collegeRepository.existsBySlug("st-george")).thenReturn(false);
        when(countryRepository.findBySlug("uk")).thenReturn(Optional.of(countryEntity));
        when(universityRepository.findBySlug("london-medical")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> collegeService.createCollege(collegeDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should update college successfully, increment counter, and record timer")
    void testUpdateCollege_Success() {
        when(collegeRepository.findBySlug("st-george")).thenReturn(Optional.of(collegeEntity));
        when(countryRepository.findBySlug("uk")).thenReturn(Optional.of(countryEntity));
        when(universityRepository.findBySlug("london-medical")).thenReturn(Optional.of(universityEntity));
        when(courseRepository.findBySlugIn(any())).thenReturn(List.of(new Course()));
        when(collegeRepository.save(any(College.class))).thenReturn(collegeEntity);
        when(collegeMapper.toDTO(collegeEntity)).thenReturn(collegeDTO);

        CollegeDTO result = collegeService.updateCollege("st-george", collegeDTO);

        assertThat(result).isNotNull();
        verify(collegeRepository, times(1)).findBySlug("st-george");
        verify(collegeRepository, times(1)).save(any(College.class));

        assertThat(meterRegistry.get("business.college.updated.total").counter().count()).isEqualTo(1.0);
        assertThat(meterRegistry.get("db.query.college.save.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should update college with null university successfully")
    void testUpdateCollege_NullUniversity() {
        collegeDTO.setUniversity(null);
        when(collegeRepository.findBySlug("st-george")).thenReturn(Optional.of(collegeEntity));
        when(countryRepository.findBySlug("uk")).thenReturn(Optional.of(countryEntity));
        when(collegeRepository.save(any(College.class))).thenReturn(collegeEntity);
        when(collegeMapper.toDTO(collegeEntity)).thenReturn(collegeDTO);

        CollegeDTO result = collegeService.updateCollege("st-george", collegeDTO);

        assertThat(result).isNotNull();
        assertThat(collegeEntity.getUniversity()).isNull();
    }

    @Test
    @DisplayName("Should update college with null offeredCourses successfully")
    void testUpdateCollege_NullOfferedCourses() {
        collegeDTO.setOfferedCourses(null);
        when(collegeRepository.findBySlug("st-george")).thenReturn(Optional.of(collegeEntity));
        when(countryRepository.findBySlug("uk")).thenReturn(Optional.of(countryEntity));
        when(universityRepository.findBySlug("london-medical")).thenReturn(Optional.of(universityEntity));
        when(collegeRepository.save(any(College.class))).thenReturn(collegeEntity);
        when(collegeMapper.toDTO(collegeEntity)).thenReturn(collegeDTO);

        CollegeDTO result = collegeService.updateCollege("st-george", collegeDTO);

        assertThat(result).isNotNull();
        assertThat(collegeEntity.getOfferedCourses()).isEmpty();
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent college slug")
    void testUpdateCollege_NotFound() {
        when(collegeRepository.findBySlug("invalid")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> collegeService.updateCollege("invalid", collegeDTO))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(collegeRepository, times(1)).findBySlug("invalid");
        verify(collegeRepository, never()).save(any(College.class));
    }

    @Test
    @DisplayName("Should soft delete college successfully, increment counter, and record timer")
    void testDeleteCollege_Success() {
        when(collegeRepository.findBySlug("st-george")).thenReturn(Optional.of(collegeEntity));
        when(collegeRepository.save(any(College.class))).thenReturn(collegeEntity);

        collegeService.deleteCollege("st-george");

        ArgumentCaptor<College> captor = ArgumentCaptor.forClass(College.class);
        verify(collegeRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().isActive()).isFalse();

        assertThat(meterRegistry.get("business.college.deleted.total").counter().count()).isEqualTo(1.0);
        assertThat(meterRegistry.get("db.query.college.save.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent college slug")
    void testDeleteCollege_NotFound() {
        when(collegeRepository.findBySlug("invalid")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> collegeService.deleteCollege("invalid"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(collegeRepository, times(1)).findBySlug("invalid");
        verify(collegeRepository, never()).save(any(College.class));
    }
}