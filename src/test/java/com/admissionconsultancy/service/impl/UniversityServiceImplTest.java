package com.admissionconsultancy.service.impl;

import com.admissionconsultancy.dto.CountrySummaryDTO;
import com.admissionconsultancy.dto.CourseSummaryDTO;
import com.admissionconsultancy.dto.UniversityDTO;
import com.admissionconsultancy.entity.Country;
import com.admissionconsultancy.entity.Course;
import com.admissionconsultancy.entity.University;
import com.admissionconsultancy.exception.DuplicateResourceException;
import com.admissionconsultancy.exception.ResourceNotFoundException;
import com.admissionconsultancy.mapper.UniversityMapper;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UniversityServiceImplTest {

    @Mock
    private UniversityRepository universityRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UniversityMapper universityMapper;

    private MeterRegistry meterRegistry;
    private UniversityServiceImpl universityService;

    private University universityEntity;
    private UniversityDTO universityDTO;
    private Country countryEntity;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        universityService = new UniversityServiceImpl(
                universityRepository,
                countryRepository,
                courseRepository,
                universityMapper,
                meterRegistry
        );

        countryEntity = new Country();
        countryEntity.setName("United Kingdom");
        countryEntity.setSlug("uk");

        universityEntity = new University();
        universityEntity.setName("Oxford University");
        universityEntity.setSlug("oxford");
        universityEntity.setActive(true);
        universityEntity.setCountry(countryEntity);

        CountrySummaryDTO countrySummaryDTO = new CountrySummaryDTO();
        countrySummaryDTO.setSlug("uk");

        CourseSummaryDTO courseSummaryDTO = new CourseSummaryDTO();
        courseSummaryDTO.setSlug("mbbs");

        universityDTO = new UniversityDTO();
        universityDTO.setName("Oxford University");
        universityDTO.setSlug("oxford");
        universityDTO.setCountry(countrySummaryDTO);
        universityDTO.setOfferedCourses(List.of(courseSummaryDTO));
    }

    @Test
    @DisplayName("Should fetch all active universities successfully and record timer")
    void testGetActiveUniversities() {
        when(universityRepository.findAllByIsActiveTrue()).thenReturn(List.of(universityEntity));
        when(universityMapper.toDTO(universityEntity)).thenReturn(universityDTO);

        List<UniversityDTO> result = universityService.getActiveUniversities();

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Oxford University");
        verify(universityRepository, times(1)).findAllByIsActiveTrue();
        assertThat(meterRegistry.get("db.query.university.fetch_all.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should fetch all universities for admin successfully and record timer")
    void testGetAllUniversitiesAdmin() {
        when(universityRepository.findAll()).thenReturn(List.of(universityEntity));
        when(universityMapper.toDTO(universityEntity)).thenReturn(universityDTO);

        List<UniversityDTO> result = universityService.getAllUniversitiesAdmin();

        assertThat(result).isNotNull().hasSize(1);
        verify(universityRepository, times(1)).findAll();
        assertThat(meterRegistry.get("db.query.university.fetch_all.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should fetch universities by country slug successfully and record timer")
    void testGetUniversitiesByCountry() {
        when(universityRepository.findByCountrySlugAndIsActiveTrue("uk")).thenReturn(List.of(universityEntity));
        when(universityMapper.toDTO(universityEntity)).thenReturn(universityDTO);

        List<UniversityDTO> result = universityService.getUniversitiesByCountry("uk");

        assertThat(result).isNotNull().hasSize(1);
        verify(universityRepository, times(1)).findByCountrySlugAndIsActiveTrue("uk");
        assertThat(meterRegistry.get("db.query.university.fetch_by_country.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should fetch university by slug successfully and record timer")
    void testGetBySlug_Success() {
        when(universityRepository.findBySlugAndIsActiveTrue("oxford")).thenReturn(Optional.of(universityEntity));
        when(universityMapper.toDTO(universityEntity)).thenReturn(universityDTO);

        UniversityDTO result = universityService.getBySlug("oxford");

        assertThat(result).isNotNull();
        assertThat(result.getSlug()).isEqualTo("oxford");
        verify(universityRepository, times(1)).findBySlugAndIsActiveTrue("oxford");
        assertThat(meterRegistry.get("db.query.university.fetch_by_slug.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when slug is not found")
    void testGetBySlug_NotFound() {
        when(universityRepository.findBySlugAndIsActiveTrue("invalid")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> universityService.getBySlug("invalid"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(universityRepository, times(1)).findBySlugAndIsActiveTrue("invalid");
    }

    @Test
    @DisplayName("Should create university successfully, increment counter, and record timer")
    void testCreateUniversity_Success() {
        when(universityRepository.existsBySlug("oxford")).thenReturn(false);
        when(countryRepository.findBySlug("uk")).thenReturn(Optional.of(countryEntity));
        when(courseRepository.findBySlugIn(any())).thenReturn(List.of(new Course()));
        when(universityRepository.save(any(University.class))).thenReturn(universityEntity);
        when(universityMapper.toDTO(universityEntity)).thenReturn(universityDTO);

        UniversityDTO result = universityService.createUniversity(universityDTO);

        assertThat(result).isNotNull();
        verify(universityRepository, times(1)).existsBySlug("oxford");
        verify(countryRepository, times(1)).findBySlug("uk");
        verify(universityRepository, times(1)).save(any(University.class));

        assertThat(meterRegistry.get("business.university.created.total").counter().count()).isEqualTo(1.0);
        assertThat(meterRegistry.get("db.query.university.save.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when creating university with duplicate slug")
    void testCreateUniversity_DuplicateSlug() {
        when(universityRepository.existsBySlug("oxford")).thenReturn(true);

        assertThatThrownBy(() -> universityService.createUniversity(universityDTO))
                .isInstanceOf(DuplicateResourceException.class);

        verify(universityRepository, times(1)).existsBySlug("oxford");
        verify(universityRepository, never()).save(any(University.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when country slug does not exist on create")
    void testCreateUniversity_CountryNotFound() {
        when(universityRepository.existsBySlug("oxford")).thenReturn(false);
        when(countryRepository.findBySlug("uk")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> universityService.createUniversity(universityDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should update university successfully, increment counter, and record timer")
    void testUpdateUniversity_Success() {
        when(universityRepository.findBySlug("oxford")).thenReturn(Optional.of(universityEntity));
        when(countryRepository.findBySlug("uk")).thenReturn(Optional.of(countryEntity));
        when(courseRepository.findBySlugIn(any())).thenReturn(List.of(new Course()));
        when(universityRepository.save(any(University.class))).thenReturn(universityEntity);
        when(universityMapper.toDTO(universityEntity)).thenReturn(universityDTO);

        UniversityDTO result = universityService.updateUniversity("oxford", universityDTO);

        assertThat(result).isNotNull();
        verify(universityRepository, times(1)).findBySlug("oxford");
        verify(universityRepository, times(1)).save(any(University.class));

        assertThat(meterRegistry.get("business.university.updated.total").counter().count()).isEqualTo(1.0);
        assertThat(meterRegistry.get("db.query.university.save.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent university slug")
    void testUpdateUniversity_NotFound() {
        when(universityRepository.findBySlug("invalid")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> universityService.updateUniversity("invalid", universityDTO))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(universityRepository, times(1)).findBySlug("invalid");
        verify(universityRepository, never()).save(any(University.class));
    }

    @Test
    @DisplayName("Should soft delete university successfully, increment counter, and record timer")
    void testDeleteUniversity_Success() {
        when(universityRepository.findBySlug("oxford")).thenReturn(Optional.of(universityEntity));
        when(universityRepository.save(any(University.class))).thenReturn(universityEntity);

        universityService.deleteUniversity("oxford");

        ArgumentCaptor<University> captor = ArgumentCaptor.forClass(University.class);
        verify(universityRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().isActive()).isFalse();

        assertThat(meterRegistry.get("business.university.deleted.total").counter().count()).isEqualTo(1.0);
        assertThat(meterRegistry.get("db.query.university.save.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent university slug")
    void testDeleteUniversity_NotFound() {
        when(universityRepository.findBySlug("invalid")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> universityService.deleteUniversity("invalid"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(universityRepository, times(1)).findBySlug("invalid");
        verify(universityRepository, never()).save(any(University.class));
    }
}