package com.admissionconsultancy.service.impl;

import com.admissionconsultancy.dto.CourseDTO;
import com.admissionconsultancy.entity.Course;
import com.admissionconsultancy.exception.DuplicateResourceException;
import com.admissionconsultancy.exception.ResourceNotFoundException;
import com.admissionconsultancy.mapper.CourseMapper;
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
class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseMapper courseMapper;

    @Mock
    UniversityRepository universityRepository;

    private MeterRegistry meterRegistry;
    private CourseServiceImpl courseService;

    private Course courseEntity;
    private CourseDTO courseDTO;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        courseService = new CourseServiceImpl(courseRepository, universityRepository, courseMapper, meterRegistry);

        courseEntity = new Course();
        courseEntity.setName("MBBS");
        courseEntity.setSlug("mbbs");
        courseEntity.setActive(true);

        courseDTO = new CourseDTO();
        courseDTO.setName("MBBS");
        courseDTO.setSlug("mbbs");
    }

    @Test
    @DisplayName("Should fetch all active courses successfully and record timer")
    void testGetActiveCourses() {
        when(courseRepository.findAllByIsActiveTrue()).thenReturn(List.of(courseEntity));
        when(courseMapper.toDTO(courseEntity)).thenReturn(courseDTO);

        List<CourseDTO> result = courseService.getActiveCourses();

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("MBBS");
        verify(courseRepository, times(1)).findAllByIsActiveTrue();
        assertThat(meterRegistry.get("db.query.course.fetch_active.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should fetch all courses for admin successfully and record timer")
    void testGetAllCoursesAdmin() {
        when(courseRepository.findAll()).thenReturn(List.of(courseEntity));
        when(courseMapper.toDTO(courseEntity)).thenReturn(courseDTO);

        List<CourseDTO> result = courseService.getAllCoursesAdmin();

        assertThat(result).isNotNull().hasSize(1);
        verify(courseRepository, times(1)).findAll();
        assertThat(meterRegistry.get("db.query.course.fetch_all_admin.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should fetch course by slug successfully and record timer")
    void testGetBySlug_Success() {
        when(courseRepository.findBySlugAndIsActiveTrue("mbbs")).thenReturn(Optional.of(courseEntity));
        when(courseMapper.toDTO(courseEntity)).thenReturn(courseDTO);

        CourseDTO result = courseService.getBySlug("mbbs");

        assertThat(result).isNotNull();
        assertThat(result.getSlug()).isEqualTo("mbbs");
        verify(courseRepository, times(1)).findBySlugAndIsActiveTrue("mbbs");
        assertThat(meterRegistry.get("db.query.course.fetch_by_slug.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when course slug is not found")
    void testGetBySlug_NotFound() {
        when(courseRepository.findBySlugAndIsActiveTrue("invalid")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.getBySlug("invalid"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(courseRepository, times(1)).findBySlugAndIsActiveTrue("invalid");
    }

    @Test
    @DisplayName("Should create new course successfully, increment counter, and record timer")
    void testCreateCourse_Success() {
        when(courseRepository.existsBySlug("mbbs")).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenReturn(courseEntity);
        when(courseMapper.toDTO(courseEntity)).thenReturn(courseDTO);

        CourseDTO result = courseService.createCourse(courseDTO);

        assertThat(result).isNotNull();
        verify(courseRepository, times(1)).existsBySlug("mbbs");
        verify(courseRepository, times(1)).save(any(Course.class));

        assertThat(meterRegistry.get("business.course.created.total").counter().count()).isEqualTo(1.0);
        assertThat(meterRegistry.get("db.query.course.save.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when creating course with existing slug")
    void testCreateCourse_DuplicateSlug() {
        when(courseRepository.existsBySlug("mbbs")).thenReturn(true);

        assertThatThrownBy(() -> courseService.createCourse(courseDTO))
                .isInstanceOf(DuplicateResourceException.class);

        verify(courseRepository, times(1)).existsBySlug("mbbs");
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    @DisplayName("Should update course successfully, increment counter, and record timer")
    void testUpdateCourse_Success() {
        when(courseRepository.findBySlug("mbbs")).thenReturn(Optional.of(courseEntity));
        when(courseRepository.save(any(Course.class))).thenReturn(courseEntity);
        when(courseMapper.toDTO(courseEntity)).thenReturn(courseDTO);

        CourseDTO result = courseService.updateCourse("mbbs", courseDTO);

        assertThat(result).isNotNull();
        verify(courseRepository, times(1)).findBySlug("mbbs");
        verify(courseRepository, times(1)).save(any(Course.class));

        assertThat(meterRegistry.get("business.course.updated.total").counter().count()).isEqualTo(1.0);
        assertThat(meterRegistry.get("db.query.course.save.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent course slug")
    void testUpdateCourse_NotFound() {
        when(courseRepository.findBySlug("invalid")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.updateCourse("invalid", courseDTO))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(courseRepository, times(1)).findBySlug("invalid");
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    @DisplayName("Should soft delete course successfully, increment counter, and record timer")
    void testDeleteCourse_Success() {
        when(courseRepository.findBySlug("mbbs")).thenReturn(Optional.of(courseEntity));
        when(courseRepository.save(any(Course.class))).thenReturn(courseEntity);

        courseService.deleteCourse("mbbs");

        ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);
        verify(courseRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().isActive()).isFalse();

        assertThat(meterRegistry.get("business.course.deleted.total").counter().count()).isEqualTo(1.0);
        assertThat(meterRegistry.get("db.query.course.save.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent course slug")
    void testDeleteCourse_NotFound() {
        when(courseRepository.findBySlug("invalid")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.deleteCourse("invalid"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(courseRepository, times(1)).findBySlug("invalid");
        verify(courseRepository, never()).save(any(Course.class));
    }
}