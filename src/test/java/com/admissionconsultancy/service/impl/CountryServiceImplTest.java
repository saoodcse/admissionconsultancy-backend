package com.admissionconsultancy.service.impl;

import com.admissionconsultancy.dto.CountryDTO;
import com.admissionconsultancy.entity.Country;
import com.admissionconsultancy.exception.DuplicateResourceException;
import com.admissionconsultancy.exception.ResourceNotFoundException;
import com.admissionconsultancy.mapper.CountryMapper;
import com.admissionconsultancy.repository.CountryRepository;
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
class CountryServiceImplTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CountryMapper countryMapper;

    // Real SimpleMeterRegistry to track Counters and Timers
    private MeterRegistry meterRegistry;

    private CountryServiceImpl countryService;

    private Country countryEntity;
    private CountryDTO countryDTO;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        // Instantiate the service passing the real SimpleMeterRegistry
        countryService = new CountryServiceImpl(countryRepository, countryMapper, meterRegistry);

        countryEntity = new Country();
        countryEntity.setName("United Kingdom");
        countryEntity.setSlug("uk");
        countryEntity.setActive(true);

        countryDTO = new CountryDTO();
        countryDTO.setName("United Kingdom");
        countryDTO.setSlug("uk");
    }

    @Test
    @DisplayName("Should fetch all active countries successfully and record timer metric")
    void testGetActiveCountries() {
        when(countryRepository.findAllByIsActiveTrue()).thenReturn(List.of(countryEntity));
        when(countryMapper.toDTO(countryEntity)).thenReturn(countryDTO);

        List<CountryDTO> result = countryService.getActiveCountries();

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("United Kingdom");
        verify(countryRepository, times(1)).findAllByIsActiveTrue();

        // Verify Micrometer Timer recorded execution
        assertThat(meterRegistry.get("db.query.country.fetch_active.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should fetch all countries for admin successfully and record timer metric")
    void testGetAllCountriesAdmin() {
        when(countryRepository.findAll()).thenReturn(List.of(countryEntity));
        when(countryMapper.toDTO(countryEntity)).thenReturn(countryDTO);

        List<CountryDTO> result = countryService.getAllCountriesAdmin();

        assertThat(result).isNotNull().hasSize(1);
        verify(countryRepository, times(1)).findAll();

        // Verify Micrometer Timer recorded execution
        assertThat(meterRegistry.get("db.query.country.fetch_all_admin.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should fetch country by slug successfully and record timer metric")
    void testGetCountryBySlug_Success() {
        when(countryRepository.findBySlugAndIsActiveTrue("uk")).thenReturn(Optional.of(countryEntity));
        when(countryMapper.toDTO(countryEntity)).thenReturn(countryDTO);

        CountryDTO result = countryService.getCountryBySlug("uk");

        assertThat(result).isNotNull();
        assertThat(result.getSlug()).isEqualTo("uk");
        verify(countryRepository, times(1)).findBySlugAndIsActiveTrue("uk");

        // Verify Micrometer Timer recorded execution
        assertThat(meterRegistry.get("db.query.country.fetch_by_slug.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when slug is not found")
    void testGetCountryBySlug_NotFound() {
        when(countryRepository.findBySlugAndIsActiveTrue("invalid")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> countryService.getCountryBySlug("invalid"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(countryRepository, times(1)).findBySlugAndIsActiveTrue("invalid");
    }

    @Test
    @DisplayName("Should create a new country successfully, increment counter, and record save timer")
    void testCreateCountry_Success() {
        when(countryRepository.existsBySlug("uk")).thenReturn(false);
        when(countryRepository.save(any(Country.class))).thenReturn(countryEntity);
        when(countryMapper.toDTO(countryEntity)).thenReturn(countryDTO);

        CountryDTO result = countryService.createCountry(countryDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("United Kingdom");
        verify(countryRepository, times(1)).existsBySlug("uk");
        verify(countryRepository, times(1)).save(any(Country.class));

        // Verify Micrometer Business Counter incremented
        assertThat(meterRegistry.get("business.country.created.total").counter().count()).isEqualTo(1.0);
        // Verify Micrometer Save Timer recorded execution
        assertThat(meterRegistry.get("db.query.country.save.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when creating country with existing slug")
    void testCreateCountry_DuplicateSlug() {
        when(countryRepository.existsBySlug("uk")).thenReturn(true);

        assertThatThrownBy(() -> countryService.createCountry(countryDTO))
                .isInstanceOf(DuplicateResourceException.class);

        verify(countryRepository, times(1)).existsBySlug("uk");
        verify(countryRepository, never()).save(any(Country.class));

        // Ensure counter did not increment
        assertThat(meterRegistry.get("business.country.created.total").counter().count()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should update an existing country successfully, increment counter, and record save timer")
    void testUpdateCountry_Success() {
        when(countryRepository.findBySlug("uk")).thenReturn(Optional.of(countryEntity));
        when(countryRepository.save(any(Country.class))).thenReturn(countryEntity);
        when(countryMapper.toDTO(countryEntity)).thenReturn(countryDTO);

        CountryDTO result = countryService.updateCountry("uk", countryDTO);

        assertThat(result).isNotNull();
        verify(countryRepository, times(1)).findBySlug("uk");
        verify(countryRepository, times(1)).save(any(Country.class));

        // Verify Micrometer Update Counter incremented
        assertThat(meterRegistry.get("business.country.updated.total").counter().count()).isEqualTo(1.0);
        // Verify Micrometer Save Timer recorded execution
        assertThat(meterRegistry.get("db.query.country.save.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent country slug")
    void testUpdateCountry_NotFound() {
        when(countryRepository.findBySlug("invalid")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> countryService.updateCountry("invalid", countryDTO))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(countryRepository, times(1)).findBySlug("invalid");
        verify(countryRepository, never()).save(any(Country.class));
    }

    @Test
    @DisplayName("Should soft-delete a country successfully, increment counter, and record save timer")
    void testDeleteCountry_Success() {
        when(countryRepository.findBySlug("uk")).thenReturn(Optional.of(countryEntity));
        when(countryRepository.save(any(Country.class))).thenReturn(countryEntity);

        countryService.deleteCountry("uk");

        ArgumentCaptor<Country> countryCaptor = ArgumentCaptor.forClass(Country.class);
        verify(countryRepository, times(1)).save(countryCaptor.capture());

        // Assert that isActive was set to false (soft delete)
        assertThat(countryCaptor.getValue().isActive()).isFalse();

        // Verify Micrometer Delete Counter incremented
        assertThat(meterRegistry.get("business.country.deleted.total").counter().count()).isEqualTo(1.0);
        // Verify Micrometer Save Timer recorded execution
        assertThat(meterRegistry.get("db.query.country.save.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent country slug")
    void testDeleteCountry_NotFound() {
        when(countryRepository.findBySlug("invalid")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> countryService.deleteCountry("invalid"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(countryRepository, times(1)).findBySlug("invalid");
        verify(countryRepository, never()).save(any(Country.class));
    }
}