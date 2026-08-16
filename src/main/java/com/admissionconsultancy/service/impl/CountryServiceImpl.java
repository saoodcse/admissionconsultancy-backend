package com.admissionconsultancy.service.impl;

import com.admissionconsultancy.constants.AppCode;
import com.admissionconsultancy.dto.CountryDTO;
import com.admissionconsultancy.entity.Country;
import com.admissionconsultancy.exception.DuplicateResourceException;
import com.admissionconsultancy.exception.ResourceNotFoundException;
import com.admissionconsultancy.mapper.CountryMapper;
import com.admissionconsultancy.repository.CountryRepository;
import com.admissionconsultancy.service.CountryService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;

    // Counters for Business Operations
    private final Counter countryCreatedCounter;
    private final Counter countryUpdatedCounter;
    private final Counter countryDeletedCounter;

    // Timers for Database Query Execution Times
    private final Timer fetchActiveQueryTimer;
    private final Timer fetchAllAdminQueryTimer;
    private final Timer fetchBySlugQueryTimer;
    private final Timer saveDbQueryTimer;

    public CountryServiceImpl(
            CountryRepository countryRepository,
            CountryMapper countryMapper,
            MeterRegistry meterRegistry) {

        this.countryRepository = countryRepository;
        this.countryMapper = countryMapper;

        // Initialize Counters (Business Metrics)
        this.countryCreatedCounter = Counter.builder("business.country.created.total")
                .description("Total number of countries created")
                .register(meterRegistry);
        this.countryUpdatedCounter = Counter.builder("business.country.updated.total")
                .description("Total number of countries updated")
                .register(meterRegistry);
        this.countryDeletedCounter = Counter.builder("business.country.deleted.total")
                .description("Total number of countries soft-deleted")
                .register(meterRegistry);

        // Initialize Timers (Performance/DB Metrics)
        this.fetchActiveQueryTimer = Timer.builder("db.query.country.fetch_active.time")
                .description("Time taken to fetch all active countries")
                .register(meterRegistry);
        this.fetchAllAdminQueryTimer = Timer.builder("db.query.country.fetch_all_admin.time")
                .description("Time taken to fetch all countries for admin")
                .register(meterRegistry);
        this.fetchBySlugQueryTimer = Timer.builder("db.query.country.fetch_by_slug.time")
                .description("Time taken to fetch a single country by slug")
                .register(meterRegistry);
        this.saveDbQueryTimer = Timer.builder("db.query.country.save.time")
                .description("Time taken to persist or update a country in the database")
                .register(meterRegistry);
    }

    /**
     * Fetches all active countries for the public-facing frontend.
     */
    @Override
    @Transactional(readOnly = true) // Keeps session open to map lazy collections safely
    public List<CountryDTO> getActiveCountries() {
        log.info("Fetching all active countries from the database");

        // Record Exact DB Query Time
        List<Country> countries = fetchActiveQueryTimer.record(countryRepository::findAllByIsActiveTrue);

        return countries.stream()
                .map(countryMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Fetches all countries (including inactive ones) for the Admin Dashboard.
     */
    @Override
    @Transactional(readOnly = true) //  Keeps session open to map lazy collections safely
    public List<CountryDTO> getAllCountriesAdmin() {
        log.info("Admin: Fetching all countries (including inactive)");

        // Record Exact DB Query Time
        List<Country> countries = fetchAllAdminQueryTimer.record(() -> countryRepository.findAll());

        return countries.stream()
                .map(countryMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Fetches a single active country by its slug (e.g., for the /country/{slug} page).
     */
    @Override
    @Transactional(readOnly = true) // Keeps session open to map lazy collections safely
    public CountryDTO getCountryBySlug(String slug) {
        log.info("Fetching country details for slug: '{}'", slug);

        // Record Exact DB Query Time
        Country country = fetchBySlugQueryTimer.record(() ->
                        countryRepository.findBySlugAndIsActiveTrue(slug))
                .orElseThrow(() -> {
                    log.warn("Country not found for slug: '{}'", slug);
                    return new ResourceNotFoundException(AppCode.COUNTRY_NOT_FOUND.getMessage());
                });

        return countryMapper.toDTO(country);
    }

    /**
     * Creates a new Country from the Admin panel.
     */
    @Override
    @Transactional
    public CountryDTO createCountry(CountryDTO countryDTO) {
        log.info("Attempting to create new country: '{}'", countryDTO.getName());

        if (countryRepository.existsBySlug(countryDTO.getSlug())) {
            log.error("Creation failed. Country slug '{}' already exists.", countryDTO.getSlug());
            throw new DuplicateResourceException(AppCode.COUNTRY_ALREADY_EXISTS);
        }

        // 1. Map DTO to a new Entity
        Country countryEntity = new Country();
        countryMapper.updateEntityFromDTO(countryDTO, countryEntity);

        // 2. Ensure it is active by default and bidirectional links are set
        countryEntity.setActive(true);
        countryEntity.syncChildren();

        // 3. Save to DB (Record Exact DB Insertion Time)
        Country savedCountry = saveDbQueryTimer.record(() -> countryRepository.save(countryEntity));

        log.info("Successfully created country: '{}' with slug: {}", savedCountry.getName(), savedCountry.getSlug());

        // Increment Business Metric
        countryCreatedCounter.increment();

        // 4. Return as DTO
        return countryMapper.toDTO(savedCountry);
    }

    /**
     * Updates an existing Country entirely based on the provided DTO.
     */
    @Override
    @Transactional
    public CountryDTO updateCountry(String slug, CountryDTO countryDTO) {
        log.info("Attempting to update country with slug: {}", slug);

        // 1. Fetch existing entity
        Country existingCountry = countryRepository.findBySlug(slug)
                .orElseThrow(() -> {
                    log.warn("Update failed. Country not found with slug: {}", slug);
                    return new ResourceNotFoundException(AppCode.COUNTRY_NOT_FOUND.getMessage());
                });

        // 2. The mapper safely clears old nested collections and repopulates them,
        // ensuring orphanRemoval deletes old rows from the database.
        countryMapper.updateEntityFromDTO(countryDTO, existingCountry);

        // 3. Sync bidirectional links (parent-child) and update timestamp
        existingCountry.syncChildren();
        existingCountry.setUpdatedAt(LocalDateTime.now());

        // 4. Save and flush (Record Exact DB Update Time)
        Country savedCountry = saveDbQueryTimer.record(() -> countryRepository.save(existingCountry));

        log.info("Successfully updated country with slug: {}", slug);

        // Increment Business Metric
        countryUpdatedCounter.increment();

        // 5. Return updated DTO
        return countryMapper.toDTO(savedCountry);
    }

    /**
     * Soft-deletes a Country so it no longer appears on the public frontend.
     */
    @Override
    @Transactional
    public void deleteCountry(String slug) {
        log.info("Attempting to soft-delete country with slug: {}", slug);

        Country country = countryRepository.findBySlug(slug)
                .orElseThrow(() -> {
                    log.warn("Delete failed. Country not found with slug: {}", slug);
                    return new ResourceNotFoundException(AppCode.COUNTRY_NOT_FOUND.getMessage());
                });

        country.setActive(false);

        // Record Exact DB Update Time for Soft-Delete
        saveDbQueryTimer.record(() -> countryRepository.save(country));

        log.info("Successfully soft-deleted country with slug: {}", slug);

        // Increment Business Metric
        countryDeletedCounter.increment();
    }
}