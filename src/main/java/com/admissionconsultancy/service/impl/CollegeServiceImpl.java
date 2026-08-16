package com.admissionconsultancy.service.impl;

import com.admissionconsultancy.constants.AppCode;
import com.admissionconsultancy.dto.CollegeDTO;
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
import com.admissionconsultancy.service.CollegeService;
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
public class CollegeServiceImpl implements CollegeService {

    private final CollegeRepository collegeRepository;
    private final CountryRepository countryRepository;
    private final UniversityRepository universityRepository;
    private final CourseRepository courseRepository;
    private final CollegeMapper collegeMapper;

    // Counters for Business Operations
    private final Counter collegeCreatedCounter;
    private final Counter collegeUpdatedCounter;
    private final Counter collegeDeletedCounter;

    // Timers for Database Query Execution Times
    private final Timer fetchActiveQueryTimer;
    private final Timer fetchAllAdminQueryTimer;
    private final Timer fetchByCountryQueryTimer;
    private final Timer fetchBySlugQueryTimer;
    private final Timer saveDbQueryTimer;

    public CollegeServiceImpl(
            CollegeRepository collegeRepository,
            CountryRepository countryRepository,
            UniversityRepository universityRepository,
            CourseRepository courseRepository,
            CollegeMapper collegeMapper,
            MeterRegistry meterRegistry) {

        this.collegeRepository = collegeRepository;
        this.countryRepository = countryRepository;
        this.universityRepository = universityRepository;
        this.courseRepository = courseRepository;
        this.collegeMapper = collegeMapper;

        // Initialize Counters (Business Metrics)
        this.collegeCreatedCounter = Counter.builder("business.college.created.total")
                .description("Total number of colleges created")
                .register(meterRegistry);
        this.collegeUpdatedCounter = Counter.builder("business.college.updated.total")
                .description("Total number of colleges updated")
                .register(meterRegistry);
        this.collegeDeletedCounter = Counter.builder("business.college.deleted.total")
                .description("Total number of colleges soft-deleted")
                .register(meterRegistry);

        // Initialize Timers (Performance/DB Metrics)
        this.fetchActiveQueryTimer = Timer.builder("db.query.college.fetch_active.time")
                .description("Time taken to fetch all active colleges")
                .register(meterRegistry);
        this.fetchAllAdminQueryTimer = Timer.builder("db.query.college.fetch_all_admin.time")
                .description("Time taken to fetch all colleges for admin")
                .register(meterRegistry);
        this.fetchByCountryQueryTimer = Timer.builder("db.query.college.fetch_by_country.time")
                .description("Time taken to fetch colleges by country slug")
                .register(meterRegistry);
        this.fetchBySlugQueryTimer = Timer.builder("db.query.college.fetch_by_slug.time")
                .description("Time taken to fetch a single college by slug")
                .register(meterRegistry);
        this.saveDbQueryTimer = Timer.builder("db.query.college.save.time")
                .description("Time taken to persist or update a college in the database")
                .register(meterRegistry);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollegeDTO> getActiveColleges() {
        log.info("Fetching all active colleges");

        // Record Exact DB Query Time
        List<College> colleges = fetchActiveQueryTimer.record(collegeRepository::findAllByIsActiveTrue);

        return colleges.stream()
                .map(collegeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollegeDTO> getAllCollegesAdmin() {
        log.info("Admin: Fetching all colleges");

        // Record Exact DB Query Time
        List<College> colleges = fetchAllAdminQueryTimer.record(()->collegeRepository.findAll());

        return colleges.stream()
                .map(collegeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollegeDTO> getCollegesByCountry(String countrySlug) {
        log.info("Fetching colleges for country slug: '{}'", countrySlug);

        // Record Exact DB Query Time
        List<College> colleges = fetchByCountryQueryTimer.record(() ->
                collegeRepository.findByCountrySlugAndIsActiveTrue(countrySlug));

        return colleges.stream()
                .map(collegeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CollegeDTO getBySlug(String slug) {
        log.info("Fetching college by slug: '{}'", slug);

        // Record Exact DB Query Time
        College college = fetchBySlugQueryTimer.record(() ->
                        collegeRepository.findBySlugAndIsActiveTrue(slug))
                .orElseThrow(() -> {
                    log.warn("College not found for slug: '{}'", slug);
                    return new ResourceNotFoundException(AppCode.COLLEGE_NOT_FOUND.getMessage());
                });

        return collegeMapper.toDTO(college);
    }

    @Override
    @Transactional
    public CollegeDTO createCollege(CollegeDTO collegeDTO) {
        log.info("Creating new college: '{}'", collegeDTO.getName());

        if (collegeRepository.existsBySlug(collegeDTO.getSlug())) {
            log.error("Creation failed. College slug '{}' already exists.", collegeDTO.getSlug());
            throw new DuplicateResourceException(AppCode.BAD_REQUEST);
        }

        College collegeEntity = new College();
        collegeMapper.updateEntityFromDTO(collegeDTO, collegeEntity);

        // Link Country (Mandatory)
        if (collegeDTO.getCountry() != null && collegeDTO.getCountry().getSlug() != null) {
            Country country = countryRepository.findBySlug(collegeDTO.getCountry().getSlug())
                    .orElseThrow(() -> new ResourceNotFoundException(AppCode.COUNTRY_NOT_FOUND.getMessage()));
            collegeEntity.setCountry(country);
        } else {
            throw new IllegalArgumentException("A College must belong to a Country. 'country' with 'slug' is required.");
        }

        // Link University (Optional)
        if (collegeDTO.getUniversity() != null && collegeDTO.getUniversity().getSlug() != null) {
            University university = universityRepository.findBySlug(collegeDTO.getUniversity().getSlug())
                    .orElseThrow(() -> new ResourceNotFoundException(AppCode.UNIVERSITY_NOT_FOUND.getMessage()));
            collegeEntity.setUniversity(university);
        }

        // Link Courses (Many-to-Many)
        if (collegeDTO.getOfferedCourses() != null && !collegeDTO.getOfferedCourses().isEmpty()) {
            List<String> courseSlugs = collegeDTO.getOfferedCourses().stream().map(c -> c.getSlug()).collect(Collectors.toList());
            List<Course> courses = courseRepository.findBySlugIn(courseSlugs);
            collegeEntity.setOfferedCourses(courses);
        }

        collegeEntity.setActive(true);

        // Record Exact DB Insertion Time
        College savedCollege = saveDbQueryTimer.record(() -> collegeRepository.save(collegeEntity));

        log.info("Successfully created college with slug: {}", savedCollege.getSlug());

        // Increment Business Metric
        collegeCreatedCounter.increment();

        return collegeMapper.toDTO(savedCollege);
    }

    @Override
    @Transactional
    public CollegeDTO updateCollege(String slug, CollegeDTO collegeDTO) {
        log.info("Updating college with slug: {}", slug);

        College existingCollege = collegeRepository.findBySlug(slug)
                .orElseThrow(() -> {
                    log.warn("Update failed. College not found with slug: {}", slug);
                    return new ResourceNotFoundException(AppCode.COLLEGE_NOT_FOUND.getMessage());
                });

        collegeMapper.updateEntityFromDTO(collegeDTO, existingCollege);

        // Update Country Link
        if (collegeDTO.getCountry() != null && collegeDTO.getCountry().getSlug() != null) {
            Country country = countryRepository.findBySlug(collegeDTO.getCountry().getSlug())
                    .orElseThrow(() -> new ResourceNotFoundException(AppCode.COUNTRY_NOT_FOUND.getMessage()));
            existingCollege.setCountry(country);
        }

        // Update University Link
        if (collegeDTO.getUniversity() != null && collegeDTO.getUniversity().getSlug() != null) {
            University university = universityRepository.findBySlug(collegeDTO.getUniversity().getSlug())
                    .orElseThrow(() -> new ResourceNotFoundException(AppCode.UNIVERSITY_NOT_FOUND.getMessage()));
            existingCollege.setUniversity(university);
        } else {
            existingCollege.setUniversity(null);
        }

        // Update Courses Link
        if (collegeDTO.getOfferedCourses() != null) {
            List<String> courseSlugs = collegeDTO.getOfferedCourses().stream().map(c -> c.getSlug()).collect(Collectors.toList());
            List<Course> courses = courseRepository.findBySlugIn(courseSlugs);
            existingCollege.setOfferedCourses(courses);
        } else {
            existingCollege.getOfferedCourses().clear();
        }

        // Record Exact DB Update Time
        College savedCollege = saveDbQueryTimer.record(() -> collegeRepository.save(existingCollege));

        log.info("Successfully updated college with slug: {}", slug);

        // Increment Business Metric
        collegeUpdatedCounter.increment();

        return collegeMapper.toDTO(savedCollege);
    }

    @Override
    @Transactional
    public void deleteCollege(String slug) {
        log.info("Soft deleting college with slug: {}", slug);

        College college = collegeRepository.findBySlug(slug)
                .orElseThrow(() -> {
                    log.warn("Delete failed. College not found with slug: {}", slug);
                    return new ResourceNotFoundException(AppCode.COLLEGE_NOT_FOUND.getMessage());
                });

        college.setActive(false);

        // Record Exact DB Update Time for Soft-Delete
        saveDbQueryTimer.record(() -> collegeRepository.save(college));

        log.info("Successfully soft-deleted college with slug: {}", slug);

        // Increment Business Metric
        collegeDeletedCounter.increment();
    }
}