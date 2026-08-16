package com.admissionconsultancy.service.impl;

import com.admissionconsultancy.constants.AppCode;
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
import com.admissionconsultancy.repository.specification.UniversitySpecification;
import com.admissionconsultancy.service.UniversityService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UniversityServiceImpl implements UniversityService {

    private final UniversityRepository universityRepository;
    private final CountryRepository countryRepository;
    private final CourseRepository courseRepository;
    private final UniversityMapper universityMapper;

    //Counters for Business Operations
    private final Counter universityCreatedCounter;
    private final Counter universityUpdatedCounter;
    private final Counter universityDeletedCounter;

    // Timers for Database Query Execution Times
    private final Timer fetchAllQueryTimer;
    private final Timer fetchBySlugQueryTimer;
    private final Timer fetchByCountryQueryTimer;
    private final Timer saveDbQueryTimer;
    private final Timer filterUniversitiesTimer;

    public UniversityServiceImpl(
            UniversityRepository universityRepository,
            CountryRepository countryRepository,
            CourseRepository courseRepository,
            UniversityMapper universityMapper,
            MeterRegistry meterRegistry) {
        this.universityRepository = universityRepository;
        this.countryRepository = countryRepository;
        this.courseRepository = courseRepository;
        this.universityMapper = universityMapper;

        // Initialize Counters (Business Metrics)
        this.universityCreatedCounter = Counter.builder("business.university.created.total")
                .description("Total number of universities created")
                .register(meterRegistry);
        this.universityUpdatedCounter = Counter.builder("business.university.updated.total")
                .description("Total number of universities updated")
                .register(meterRegistry);
        this.universityDeletedCounter = Counter.builder("business.university.deleted.total")
                .description("Total number of universities soft-deleted")
                .register(meterRegistry);

        // Initialize Timers (Performance/DB Metrics)
        this.fetchAllQueryTimer = Timer.builder("db.query.university.fetch_all.time")
                .description("Time taken to execute findAll queries on Universities")
                .register(meterRegistry);
        this.fetchBySlugQueryTimer = Timer.builder("db.query.university.fetch_by_slug.time")
                .description("Time taken to fetch a single university by slug")
                .register(meterRegistry);
        this.fetchByCountryQueryTimer = Timer.builder("db.query.university.fetch_by_country.time")
                .description("Time taken to fetch universities by country slug")
                .register(meterRegistry);
        this.saveDbQueryTimer = Timer.builder("db.query.university.save.time")
                .description("Time taken to persist a university to the database")
                .register(meterRegistry);
        this.filterUniversitiesTimer = Timer.builder("db.query.university.filter.time")
                .description("Time taken to execute advanced faceted filter on universities")
                .register(meterRegistry);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UniversityDTO> getActiveUniversities() {
        log.info("Fetching all active universities");

        // Record Exact DB Query Time
        List<University> universities = fetchAllQueryTimer.record(universityRepository::findAllByIsActiveTrue);

        return universities.stream()
                .map(universityMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UniversityDTO> getAllUniversitiesAdmin() {
        log.info("Admin: Fetching all universities");

        // Record Exact DB Query Time
        List<University> universities = fetchAllQueryTimer.record(() -> universityRepository.findAll());
        return universities.stream()
                .map(universityMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UniversityDTO> getUniversitiesByCountry(String countrySlug) {
        log.info("Fetching universities for country slug: '{}'", countrySlug);

        // Record Exact DB Query Time
        List<University> universities = fetchByCountryQueryTimer.record(() ->
                universityRepository.findByCountrySlugAndIsActiveTrue(countrySlug));

        return universities.stream()
                .map(universityMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UniversityDTO> filterUniversities(String countrySlug, Double maxBudget, Integer maxRanking) {
        log.info("Filtering universities -> country: {}, maxBudget: {}, maxRanking: {}", countrySlug, maxBudget, maxRanking);

        Specification<University> spec = Specification.where(UniversitySpecification.isActive())
                .and(UniversitySpecification.hasCountrySlug(countrySlug))
                .and(UniversitySpecification.maxTuitionUsdLessThanOrEqual(maxBudget))
                .and(UniversitySpecification.hasRankingLessThanOrEqual(maxRanking));

        List<University> universities = filterUniversitiesTimer.record(() -> universityRepository.findAll(spec));

        return universities.stream()
                .map(universityMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UniversityDTO getBySlug(String slug) {
        log.info("Fetching university by slug: '{}'", slug);

        // Record Exact DB Query Time
        University university = fetchBySlugQueryTimer.record(() ->
                        universityRepository.findBySlugAndIsActiveTrue(slug))
                .orElseThrow(() -> {
                    log.warn("University not found for slug: '{}'", slug);
                    return new ResourceNotFoundException(AppCode.UNIVERSITY_NOT_FOUND.getMessage());
                });

        return universityMapper.toDTO(university);
    }

    @Override
    @Transactional
    public UniversityDTO createUniversity(UniversityDTO universityDTO) {
        log.info("Creating new university: '{}'", universityDTO.getName());

        if (universityRepository.existsBySlug(universityDTO.getSlug())) {
            log.error("Creation failed. University slug '{}' already exists.", universityDTO.getSlug());
            throw new DuplicateResourceException(AppCode.BAD_REQUEST);
        }

        University universityEntity = new University();
        universityMapper.updateEntityFromDTO(universityDTO, universityEntity);

        if (universityDTO.getCountry() != null && universityDTO.getCountry().getSlug() != null) {
            Country country = countryRepository.findBySlug(universityDTO.getCountry().getSlug())
                    .orElseThrow(() -> new ResourceNotFoundException(AppCode.COUNTRY_NOT_FOUND.getMessage()));
            universityEntity.setCountry(country);
        } else {
            throw new IllegalArgumentException("A University must belong to a Country. 'country' with 'slug' is required.");
        }

        if (universityDTO.getOfferedCourses() != null && !universityDTO.getOfferedCourses().isEmpty()) {
            List<String> courseSlugs = universityDTO.getOfferedCourses().stream().map(c -> c.getSlug()).collect(Collectors.toList());
            List<Course> courses = courseRepository.findBySlugIn(courseSlugs);
            universityEntity.setOfferedCourses(courses);
        }

        universityEntity.setActive(true);

        // Record Exact DB Insertion Time
        University savedUniversity = saveDbQueryTimer.record(() -> universityRepository.save(universityEntity));

        log.info("Successfully created university with slug: {}", savedUniversity.getSlug());
        universityCreatedCounter.increment();

        return universityMapper.toDTO(savedUniversity);
    }

    @Override
    @Transactional
    public UniversityDTO updateUniversity(String slug, UniversityDTO universityDTO) {
        log.info("Updating university with slug: {}", slug);

        University existingUniversity = universityRepository.findBySlug(slug)
                .orElseThrow(() -> {
                    log.warn("Update failed. University not found with slug: {}", slug);
                    return new ResourceNotFoundException(AppCode.UNIVERSITY_NOT_FOUND.getMessage());
                });

        universityMapper.updateEntityFromDTO(universityDTO, existingUniversity);

        if (universityDTO.getCountry() != null && universityDTO.getCountry().getSlug() != null) {
            Country country = countryRepository.findBySlug(universityDTO.getCountry().getSlug())
                    .orElseThrow(() -> new ResourceNotFoundException(AppCode.COUNTRY_NOT_FOUND.getMessage()));
            existingUniversity.setCountry(country);
        }

        if (universityDTO.getOfferedCourses() != null) {
            List<String> courseSlugs = universityDTO.getOfferedCourses().stream().map(c -> c.getSlug()).collect(Collectors.toList());
            List<Course> courses = courseRepository.findBySlugIn(courseSlugs);
            existingUniversity.setOfferedCourses(courses);
        } else {
            existingUniversity.getOfferedCourses().clear();
        }

        // Record Exact DB Update Time
        University savedUniversity = saveDbQueryTimer.record(() -> universityRepository.save(existingUniversity));

        log.info("Successfully updated university with slug: {}", slug);
        universityUpdatedCounter.increment();

        return universityMapper.toDTO(savedUniversity);
    }

    @Override
    @Transactional
    public void deleteUniversity(String slug) {
        log.info("Soft deleting university with slug: {}", slug);

        University university = universityRepository.findBySlug(slug)
                .orElseThrow(() -> {
                    log.warn("Delete failed. University not found with slug: {}", slug);
                    return new ResourceNotFoundException(AppCode.UNIVERSITY_NOT_FOUND.getMessage());
                });

        university.setActive(false);

        // Record Exact DB Update Time for Soft-Delete
        saveDbQueryTimer.record(() -> universityRepository.save(university));

        log.info("Successfully soft-deleted university with slug: {}", slug);
        universityDeletedCounter.increment();
    }
}