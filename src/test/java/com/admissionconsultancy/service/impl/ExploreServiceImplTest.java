package com.admissionconsultancy.service.impl;

import com.admissionconsultancy.dto.CountryDTO;
import com.admissionconsultancy.dto.ExploreCountryDTO;
import com.admissionconsultancy.entity.College;
import com.admissionconsultancy.entity.Country;
import com.admissionconsultancy.entity.Course;
import com.admissionconsultancy.entity.University;
import com.admissionconsultancy.exception.ResourceNotFoundException;
import com.admissionconsultancy.mapper.CountryMapper;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExploreServiceImplTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private UniversityRepository universityRepository;

    @Mock
    private CollegeRepository collegeRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CountryMapper countryMapper;

    private MeterRegistry meterRegistry;
    private ExploreServiceImpl exploreService;

    private Country countryEntity;
    private University universityEntity;
    private College collegeEntity;
    private Course courseEntity;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        exploreService = new ExploreServiceImpl(
                countryRepository,
                universityRepository,
                collegeRepository,
                courseRepository,
                countryMapper,
                meterRegistry
        );

        courseEntity = new Course();
        courseEntity.setId(10L);
        courseEntity.setName("MBBS");
        courseEntity.setSlug("mbbs");
        courseEntity.setActive(true);
        courseEntity.setDegreeType("Undergraduate");

        universityEntity = new University();
        universityEntity.setId(1L);
        universityEntity.setName("Moscow University");
        universityEntity.setSlug("moscow-uni");
        universityEntity.setActive(true);
        universityEntity.setLogo("logo.png");
        universityEntity.setOfferedCourses(List.of(courseEntity));

        collegeEntity = new College();
        collegeEntity.setId(1L);
        collegeEntity.setName("Moscow College");
        collegeEntity.setSlug("moscow-college");
        collegeEntity.setActive(true);
        collegeEntity.setLogo("college_logo.png");
        collegeEntity.setOfferedCourses(List.of(courseEntity));

        countryEntity = new Country();
        countryEntity.setId(1L);
        countryEntity.setName("Russia");
        countryEntity.setSlug("russia");
        countryEntity.setActive(true);
        countryEntity.setUniversities(List.of(universityEntity));
        countryEntity.setColleges(List.of(collegeEntity));
    }

    @Test
    @DisplayName("Should fetch all explore countries successfully and record timer")
    void testGetAllExploreCountries() {
        when(countryRepository.findAllByIsActiveTrue()).thenReturn(List.of(countryEntity));
        when(countryMapper.toDTO(countryEntity)).thenReturn(new CountryDTO());

        List<ExploreCountryDTO> result = exploreService.getAllExploreCountries();

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getUniversities()).hasSize(1);
        assertThat(result.get(0).getColleges()).hasSize(1);
        assertThat(result.get(0).getCourses()).hasSize(1);

        verify(countryRepository, times(1)).findAllByIsActiveTrue();
        assertThat(meterRegistry.get("db.query.explore.fetch_all_countries.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should fetch explore country by slug successfully, increment view counter, and record timer")
    void testGetExploreCountryBySlug_Success() {
        when(countryRepository.findBySlugAndIsActiveTrue("russia")).thenReturn(Optional.of(countryEntity));
        when(countryMapper.toDTO(countryEntity)).thenReturn(new CountryDTO());

        ExploreCountryDTO result = exploreService.getExploreCountryBySlug("russia");

        assertThat(result).isNotNull();
        verify(countryRepository, times(1)).findBySlugAndIsActiveTrue("russia");

        assertThat(meterRegistry.get("business.explore.country_view.total").counter().count()).isEqualTo(1.0);
        assertThat(meterRegistry.get("db.query.explore.fetch_country_by_slug.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when explore country slug is missing")
    void testGetExploreCountryBySlug_NotFound() {
        when(countryRepository.findBySlugAndIsActiveTrue("invalid")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> exploreService.getExploreCountryBySlug("invalid"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(countryRepository, times(1)).findBySlugAndIsActiveTrue("invalid");
    }

    @Test
    @DisplayName("Should execute global search across all domains, increment counter, and record timers")
    void testGlobalSearch_Success() {
        when(countryRepository.findByNameContainingIgnoreCaseAndIsActiveTrue("Russia")).thenReturn(List.of(countryEntity));
        when(universityRepository.findByNameContainingIgnoreCaseAndIsActiveTrue("Russia")).thenReturn(List.of(universityEntity));
        when(collegeRepository.findByNameContainingIgnoreCaseAndIsActiveTrue("Russia")).thenReturn(List.of(collegeEntity));
        when(courseRepository.findByNameContainingIgnoreCaseAndIsActiveTrue("Russia")).thenReturn(List.of(courseEntity));

        Map<String, Object> results = exploreService.globalSearch("Russia");

        assertThat(results).isNotNull();
        assertThat(results.get("countries")).isNotNull();
        assertThat(results.get("universities")).isNotNull();
        assertThat(results.get("colleges")).isNotNull();
        assertThat(results.get("courses")).isNotNull();

        assertThat(meterRegistry.get("business.explore.search.total").counter().count()).isEqualTo(1.0);
        assertThat(meterRegistry.get("db.query.explore.search_countries.time").timer().count()).isEqualTo(1);
        assertThat(meterRegistry.get("db.query.explore.search_universities.time").timer().count()).isEqualTo(1);
        assertThat(meterRegistry.get("db.query.explore.search_colleges.time").timer().count()).isEqualTo(1);
        assertThat(meterRegistry.get("db.query.explore.search_courses.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should handle null query safely in global search")
    void testGlobalSearch_NullQuery() {
        when(countryRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(anyString())).thenReturn(List.of());
        when(universityRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(anyString())).thenReturn(List.of());
        when(collegeRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(anyString())).thenReturn(List.of());
        when(courseRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(anyString())).thenReturn(List.of());

        Map<String, Object> results = exploreService.globalSearch(null);

        assertThat(results).isNotNull();
        assertThat(meterRegistry.get("business.explore.search.total").counter().count()).isEqualTo(1.0);
    }
}