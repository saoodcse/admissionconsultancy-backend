package com.admissionconsultancy.service.impl;

import com.admissionconsultancy.constants.AppCode;
import com.admissionconsultancy.dto.CountrySummaryDTO;
import com.admissionconsultancy.dto.CourseSummaryDTO;
import com.admissionconsultancy.dto.ExploreCountryDTO;
import com.admissionconsultancy.dto.UniversitySummaryDTO;
import com.admissionconsultancy.dto.CollegeSummaryDTO;
import com.admissionconsultancy.entity.Country;
import com.admissionconsultancy.entity.Course;
import com.admissionconsultancy.entity.University;
import com.admissionconsultancy.entity.College;
import com.admissionconsultancy.exception.ResourceNotFoundException;
import com.admissionconsultancy.mapper.CountryMapper;
import com.admissionconsultancy.repository.CountryRepository;
import com.admissionconsultancy.repository.CourseRepository;
import com.admissionconsultancy.repository.UniversityRepository;
import com.admissionconsultancy.repository.CollegeRepository;
import com.admissionconsultancy.service.ExploreService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExploreServiceImpl implements ExploreService {

    private final CountryRepository countryRepository;
    private final UniversityRepository universityRepository;
    private final CollegeRepository collegeRepository; // Injected CollegeRepository
    private final CourseRepository courseRepository;
    private final CountryMapper countryMapper;

    // Counters for Business Metrics
    private final Counter globalSearchCounter;
    private final Counter exploreCountryViewCounter;

    // Timers for Database Query Performance
    private final Timer fetchAllCountriesTimer;
    private final Timer fetchCountryBySlugTimer;
    private final Timer searchCountriesTimer;
    private final Timer searchUniversitiesTimer;
    private final Timer searchCollegesTimer; // Added Timer for Colleges
    private final Timer searchCoursesTimer;

    public ExploreServiceImpl(
            CountryRepository countryRepository,
            UniversityRepository universityRepository,
            CollegeRepository collegeRepository, // Added to constructor
            CourseRepository courseRepository,
            CountryMapper countryMapper,
            MeterRegistry meterRegistry) {

        this.countryRepository = countryRepository;
        this.universityRepository = universityRepository;
        this.collegeRepository = collegeRepository;
        this.courseRepository = courseRepository;
        this.countryMapper = countryMapper;

        // Initialize Business Metric Counters
        this.globalSearchCounter = Counter.builder("business.explore.search.total")
                .description("Total number of global searches performed by users")
                .register(meterRegistry);
        this.exploreCountryViewCounter = Counter.builder("business.explore.country_view.total")
                .description("Total number of times a specific country's explore page was viewed")
                .register(meterRegistry);

        // Initialize DB Performance Timers
        this.fetchAllCountriesTimer = Timer.builder("db.query.explore.fetch_all_countries.time")
                .description("Time taken to fetch all active countries for explore view")
                .register(meterRegistry);
        this.fetchCountryBySlugTimer = Timer.builder("db.query.explore.fetch_country_by_slug.time")
                .description("Time taken to fetch a single country by slug for explore view")
                .register(meterRegistry);
        this.searchCountriesTimer = Timer.builder("db.query.explore.search_countries.time")
                .description("Time taken to search countries by name")
                .register(meterRegistry);
        this.searchUniversitiesTimer = Timer.builder("db.query.explore.search_universities.time")
                .description("Time taken to search universities by name")
                .register(meterRegistry);
        this.searchCollegesTimer = Timer.builder("db.query.explore.search_colleges.time")
                .description("Time taken to search colleges by name")
                .register(meterRegistry); // Registered College Timer
        this.searchCoursesTimer = Timer.builder("db.query.explore.search_courses.time")
                .description("Time taken to search courses by name")
                .register(meterRegistry);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExploreCountryDTO> getAllExploreCountries() {
        log.info("Fetching all explore countries data");

        // Record Exact DB Query Time
        List<Country> countries = fetchAllCountriesTimer.record(countryRepository::findAllByIsActiveTrue);

        return countries.stream()
                .map(this::buildExploreCountryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ExploreCountryDTO getExploreCountryBySlug(String slug) {
        log.info("Fetching explore country data for slug: '{}'", slug);

        // Record Exact DB Query Time
        Country country = fetchCountryBySlugTimer.record(() ->
                        countryRepository.findBySlugAndIsActiveTrue(slug))
                .orElseThrow(() -> {
                    log.warn("Explore Country not found for slug: '{}'", slug);
                    return new ResourceNotFoundException(AppCode.COUNTRY_NOT_FOUND.getMessage());
                });

        // Increment Business Metric
        exploreCountryViewCounter.increment();

        return buildExploreCountryDTO(country);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> globalSearch(String query) {
        String safeQuery = (query != null) ? query.trim() : "";
        log.info("Executing global database search for query: '{}'", safeQuery);

        // Increment Business Metric
        globalSearchCounter.increment();

        // Fetch matching records directly from the database using SQL LIKE clauses and record execution times
        List<Country> matchedCountries = searchCountriesTimer.record(() ->
                countryRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(safeQuery));

        List<University> matchedUniversities = searchUniversitiesTimer.record(() ->
                universityRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(safeQuery));

        // Added College DB Search
        List<College> matchedColleges = searchCollegesTimer.record(() ->
                collegeRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(safeQuery));

        List<Course> matchedCourses = searchCoursesTimer.record(() ->
                courseRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(safeQuery));

        // Map them to lightweight Summary DTOs for the search dropdown/results page
        List<CountrySummaryDTO> countrySummaries = matchedCountries.stream().map(c -> {
            CountrySummaryDTO dto = new CountrySummaryDTO();
            dto.setName(c.getName());
            dto.setSlug(c.getSlug());
            dto.setFlag(c.getFlag());
            return dto;
        }).collect(Collectors.toList());

        List<UniversitySummaryDTO> universitySummaries = matchedUniversities.stream().map(u -> {
            UniversitySummaryDTO dto = new UniversitySummaryDTO();
            dto.setName(u.getName());
            dto.setSlug(u.getSlug());
            dto.setLogo(u.getLogo());
            if (u.getCountry() != null) dto.setCountryName(u.getCountry().getName());
            return dto;
        }).collect(Collectors.toList());

        // Added College Mapping
        List<CollegeSummaryDTO> collegeSummaries = matchedColleges.stream().map(c -> {
            CollegeSummaryDTO dto = new CollegeSummaryDTO();
            dto.setName(c.getName());
            dto.setSlug(c.getSlug());
            // Assuming College has a logo property similar to University
            dto.setLogo(c.getLogo());
            if (c.getCountry() != null) dto.setCountryName(c.getCountry().getName());
            return dto;
        }).collect(Collectors.toList());

        List<CourseSummaryDTO> courseSummaries = matchedCourses.stream().map(c -> {
            CourseSummaryDTO dto = new CourseSummaryDTO();
            dto.setName(c.getName());
            dto.setSlug(c.getSlug());
            dto.setDegreeType(c.getDegreeType());
            return dto;
        }).collect(Collectors.toList());

        // Return a map structured for the frontend
        Map<String, Object> searchResults = new HashMap<>();
        searchResults.put("countries", countrySummaries);
        searchResults.put("universities", universitySummaries);
        searchResults.put("colleges", collegeSummaries); // Added Colleges to Response
        searchResults.put("courses", courseSummaries);

        return searchResults;
    }

    /**
     * Helper method to build a rich Explore profile.
     * Extracts courses automatically from the country's linked universities and colleges.
     */
    private ExploreCountryDTO buildExploreCountryDTO(Country country) {
        ExploreCountryDTO exploreDTO = new ExploreCountryDTO();

        // 1. Map full country details
        exploreDTO.setCountry(countryMapper.toDTO(country));

        Set<Course> uniqueCoursesInCountry = new HashSet<>();

        // 2. Map Universities associated with this country
        List<UniversitySummaryDTO> universitySummaries = new ArrayList<>();
        if (country.getUniversities() != null) {
            for (University u : country.getUniversities()) {
                if (!u.isActive()) continue; // Skip inactive universities

                // Build University Summary
                UniversitySummaryDTO uniDto = new UniversitySummaryDTO();
                uniDto.setName(u.getName());
                uniDto.setSlug(u.getSlug());
                uniDto.setLogo(u.getLogo());
                uniDto.setCountryName(country.getName());
                universitySummaries.add(uniDto);

                // Collect unique courses offered at this university
                if (u.getOfferedCourses() != null) {
                    u.getOfferedCourses().stream()
                            .filter(Course::isActive)
                            .forEach(uniqueCoursesInCountry::add);
                }
            }
        }
        exploreDTO.setUniversities(universitySummaries);

        // 3. Map Colleges associated with this country
        List<CollegeSummaryDTO> collegeSummaries = new ArrayList<>();
        if (country.getColleges() != null) { // Requires `getColleges()` mapped in Country entity
            for (College c : country.getColleges()) {
                if (!c.isActive()) continue; // Skip inactive colleges

                // Build College Summary
                CollegeSummaryDTO colDto = new CollegeSummaryDTO();
                colDto.setName(c.getName());
                colDto.setSlug(c.getSlug());
                colDto.setLogo(c.getLogo());
                colDto.setCountryName(country.getName());
                collegeSummaries.add(colDto);

                // Collect unique courses offered at this college
                if (c.getOfferedCourses() != null) {
                    c.getOfferedCourses().stream()
                            .filter(Course::isActive)
                            .forEach(uniqueCoursesInCountry::add);
                }
            }
        }
        exploreDTO.setColleges(collegeSummaries); // Requires `setColleges()` in ExploreCountryDTO

        // 4. Map the aggregated unique courses into summaries
        List<CourseSummaryDTO> courseSummaries = uniqueCoursesInCountry.stream().map(c -> {
            CourseSummaryDTO courseDto = new CourseSummaryDTO();
            courseDto.setName(c.getName());
            courseDto.setSlug(c.getSlug());
            courseDto.setDegreeType(c.getDegreeType());
            return courseDto;
        }).collect(Collectors.toList());

        exploreDTO.setCourses(courseSummaries);

        return exploreDTO;
    }
}