package com.admissionconsultancy.service.impl;

import com.admissionconsultancy.constants.AppCode;
import com.admissionconsultancy.dto.CollegeSummaryDTO;
import com.admissionconsultancy.dto.CourseDTO;
import com.admissionconsultancy.dto.UniversitySummaryDTO;
import com.admissionconsultancy.entity.College;
import com.admissionconsultancy.entity.Course;
import com.admissionconsultancy.entity.University;
import com.admissionconsultancy.exception.DuplicateResourceException;
import com.admissionconsultancy.exception.ResourceNotFoundException;
import com.admissionconsultancy.mapper.CourseMapper;
import com.admissionconsultancy.repository.CollegeRepository;
import com.admissionconsultancy.repository.CourseRepository;
import com.admissionconsultancy.repository.UniversityRepository;
import com.admissionconsultancy.service.CourseService;
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
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UniversityRepository universityRepository;
    private final CollegeRepository collegeRepository;
    private final CourseMapper courseMapper;

    // Counters for Business Operations
    private final Counter courseCreatedCounter;
    private final Counter courseUpdatedCounter;
    private final Counter courseDeletedCounter;

    // Timers for Database Query Execution Times
    private final Timer fetchAllActiveQueryTimer;
    private final Timer fetchAllAdminQueryTimer;
    private final Timer fetchBySlugQueryTimer;
    private final Timer saveDbQueryTimer;

    public CourseServiceImpl(
            CourseRepository courseRepository,
            UniversityRepository universityRepository,
            CollegeRepository collegeRepository,
            CourseMapper courseMapper,
            MeterRegistry meterRegistry) {

        this.courseRepository = courseRepository;
        this.universityRepository = universityRepository;
        this.collegeRepository = collegeRepository;
        this.courseMapper = courseMapper;

        // Initialize Counters (Business Metrics)
        this.courseCreatedCounter = Counter.builder("business.course.created.total")
                .description("Total number of courses created")
                .register(meterRegistry);
        this.courseUpdatedCounter = Counter.builder("business.course.updated.total")
                .description("Total number of courses updated")
                .register(meterRegistry);
        this.courseDeletedCounter = Counter.builder("business.course.deleted.total")
                .description("Total number of courses soft-deleted")
                .register(meterRegistry);

        // Initialize Timers (Performance/DB Metrics)
        this.fetchAllActiveQueryTimer = Timer.builder("db.query.course.fetch_active.time")
                .description("Time taken to fetch all active courses")
                .register(meterRegistry);
        this.fetchAllAdminQueryTimer = Timer.builder("db.query.course.fetch_all_admin.time")
                .description("Time taken to fetch all courses for admin")
                .register(meterRegistry);
        this.fetchBySlugQueryTimer = Timer.builder("db.query.course.fetch_by_slug.time")
                .description("Time taken to fetch a single course by slug")
                .register(meterRegistry);
        this.saveDbQueryTimer = Timer.builder("db.query.course.save.time")
                .description("Time taken to persist a course to the database")
                .register(meterRegistry);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> getActiveCourses() {
        log.info("Fetching all active courses");

        // Record Exact DB Query Time
        List<Course> courses = fetchAllActiveQueryTimer.record(courseRepository::findAllByIsActiveTrue);

        return courses.stream()
                .map(courseMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> getAllCoursesAdmin() {
        log.info("Admin: Fetching all courses (including inactive)");

        // Record Exact DB Query Time
        List<Course> courses = fetchAllAdminQueryTimer.record(() -> courseRepository.findAll());

        return courses.stream()
                .map(courseMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDTO getBySlug(String slug) {
        log.info("Fetching course details for slug: '{}'", slug);

        // Record Exact DB Query Time
        Course course = fetchBySlugQueryTimer.record(() ->
                        courseRepository.findBySlugAndIsActiveTrue(slug))
                .orElseThrow(() -> {
                    log.warn("Course not found for slug: '{}'", slug);
                    return new ResourceNotFoundException(AppCode.COURSE_NOT_FOUND.getMessage());
                });

        return courseMapper.toDTO(course);
    }

    @Override
    @Transactional
    public CourseDTO createCourse(CourseDTO courseDTO) {
        log.info("Creating new course: '{}'", courseDTO.getName());

        if (courseRepository.existsBySlug(courseDTO.getSlug())) {
            log.error("Creation failed. Course slug '{}' already exists.", courseDTO.getSlug());
            throw new DuplicateResourceException(AppCode.BAD_REQUEST);
        }

        Course courseEntity = new Course();
        courseMapper.updateEntityFromDTO(courseDTO, courseEntity);
        courseEntity.setActive(true);

        // Bidirectional synchronization for Universities
        if (courseDTO.getUniversities() != null && !courseDTO.getUniversities().isEmpty()) {
            List<String> universitySlugs = courseDTO.getUniversities().stream()
                    .map(UniversitySummaryDTO::getSlug)
                    .collect(Collectors.toList());

            List<University> universities = universityRepository.findAllBySlugIn(universitySlugs);
            for (University uni : universities) {
                uni.getOfferedCourses().add(courseEntity);
                courseEntity.getUniversities().add(uni);
            }
        }

        // Bidirectional synchronization for Colleges
        if (courseDTO.getColleges() != null && !courseDTO.getColleges().isEmpty()) {
            List<String> collegeSlugs = courseDTO.getColleges().stream()
                    .map(CollegeSummaryDTO::getSlug)
                    .collect(Collectors.toList());

            List<College> colleges = collegeRepository.findAllBySlugIn(collegeSlugs);
            for (College college : colleges) {
                college.getOfferedCourses().add(courseEntity);
                courseEntity.getColleges().add(college);
            }
        }

        Course savedCourse = saveDbQueryTimer.record(() -> courseRepository.save(courseEntity));
        log.info("Successfully created course with slug: {}", savedCourse.getSlug());
        courseCreatedCounter.increment();

        return courseMapper.toDTO(savedCourse);
    }

    @Override
    @Transactional
    public CourseDTO updateCourse(String slug, CourseDTO courseDTO) {
        log.info("Updating course with slug: {}", slug);

        Course existingCourse = courseRepository.findBySlug(slug)
                .orElseThrow(() -> {
                    log.warn("Update failed. Course not found with slug: {}", slug);
                    return new ResourceNotFoundException(AppCode.COURSE_NOT_FOUND.getMessage());
                });

        courseMapper.updateEntityFromDTO(courseDTO, existingCourse);

        // Clear old university associations and sync new ones bidirectionally
        if (courseDTO.getUniversities() != null) {
            for (University oldUni : existingCourse.getUniversities()) {
                oldUni.getOfferedCourses().remove(existingCourse);
            }
            existingCourse.getUniversities().clear();

            List<String> universitySlugs = courseDTO.getUniversities().stream()
                    .map(UniversitySummaryDTO::getSlug)
                    .collect(Collectors.toList());

            List<University> newUniversities = universityRepository.findAllBySlugIn(universitySlugs);
            for (University uni : newUniversities) {
                uni.getOfferedCourses().add(existingCourse);
                existingCourse.getUniversities().add(uni);
            }
        }

        // Clear old college associations and sync new ones bidirectionally
        if (courseDTO.getColleges() != null) {
            for (College oldCollege : existingCourse.getColleges()) {
                oldCollege.getOfferedCourses().remove(existingCourse);
            }
            existingCourse.getColleges().clear();

            List<String> collegeSlugs = courseDTO.getColleges().stream()
                    .map(CollegeSummaryDTO::getSlug)
                    .collect(Collectors.toList());

            List<College> newColleges = collegeRepository.findAllBySlugIn(collegeSlugs);
            for (College college : newColleges) {
                college.getOfferedCourses().add(existingCourse);
                existingCourse.getColleges().add(college);
            }
        }

        Course savedCourse = saveDbQueryTimer.record(() -> courseRepository.save(existingCourse));
        log.info("Successfully updated course with slug: {}", slug);
        courseUpdatedCounter.increment();

        return courseMapper.toDTO(savedCourse);
    }

    @Override
    @Transactional
    public void deleteCourse(String slug) {
        log.info("Soft deleting course with slug: {}", slug);

        Course course = courseRepository.findBySlug(slug)
                .orElseThrow(() -> {
                    log.warn("Delete failed. Course not found with slug: {}", slug);
                    return new ResourceNotFoundException(AppCode.COURSE_NOT_FOUND.getMessage());
                });

        course.setActive(false);

        // Hibernate dirty checking inside the @Transactional context will automatically
        // persist the change on transaction commit.

        log.info("Successfully soft-deleted course with slug: {}", slug);

        // Increment Business Metric
        courseDeletedCounter.increment();
    }
}