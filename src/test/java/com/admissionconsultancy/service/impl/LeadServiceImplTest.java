package com.admissionconsultancy.service.impl;

import com.admissionconsultancy.dto.CollegeSummaryDTO;
import com.admissionconsultancy.dto.CountrySummaryDTO;
import com.admissionconsultancy.dto.CourseSummaryDTO;
import com.admissionconsultancy.dto.LeadDTO;
import com.admissionconsultancy.dto.UniversitySummaryDTO;
import com.admissionconsultancy.entity.College;
import com.admissionconsultancy.entity.Country;
import com.admissionconsultancy.entity.Course;
import com.admissionconsultancy.entity.Lead;
import com.admissionconsultancy.entity.University;
import com.admissionconsultancy.enums.LeadStatus;
import com.admissionconsultancy.exception.ResourceNotFoundException;
import com.admissionconsultancy.mapper.LeadMapper;
import com.admissionconsultancy.repository.CollegeRepository;
import com.admissionconsultancy.repository.CountryRepository;
import com.admissionconsultancy.repository.CourseRepository;
import com.admissionconsultancy.repository.LeadRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeadServiceImplTest {

    @Mock
    private LeadRepository leadRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private UniversityRepository universityRepository;

    @Mock
    private CollegeRepository collegeRepository; // Added College Repository Mock

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private LeadMapper leadMapper;

    private MeterRegistry meterRegistry;
    private LeadServiceImpl leadService;

    private Lead leadEntity;
    private LeadDTO leadDTO;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        leadService = new LeadServiceImpl(
                leadRepository,
                countryRepository,
                universityRepository,
                collegeRepository,
                courseRepository,
                leadMapper,
                meterRegistry
        );

        leadEntity = new Lead();
        leadEntity.setName("John Doe");
        leadEntity.setEmail("john@example.com");
        leadEntity.setPhone("9876543210");
        leadEntity.setStatus(LeadStatus.NEW);

        CountrySummaryDTO countrySummary = new CountrySummaryDTO();
        countrySummary.setSlug("uk");

        UniversitySummaryDTO universitySummary = new UniversitySummaryDTO();
        universitySummary.setSlug("oxford");

        CollegeSummaryDTO collegeSummary = new CollegeSummaryDTO();
        collegeSummary.setSlug("st-george");

        CourseSummaryDTO courseSummary = new CourseSummaryDTO();
        courseSummary.setSlug("mbbs");

        leadDTO = new LeadDTO();
        leadDTO.setName("John Doe");
        leadDTO.setEmail("john@example.com");
        leadDTO.setPhone("9876543210");
        leadDTO.setCountry(countrySummary);
        leadDTO.setUniversity(universitySummary);
        leadDTO.setCollege(collegeSummary);
        leadDTO.setCourse(courseSummary);
    }

    @Test
    @DisplayName("Should submit lead successfully with slug relations, increment counter, and record save timer")
    void testSubmitLead_Success() {
        when(countryRepository.findBySlug("uk")).thenReturn(Optional.of(new Country()));
        when(universityRepository.findBySlug("oxford")).thenReturn(Optional.of(new University()));
        when(collegeRepository.findBySlug("st-george")).thenReturn(Optional.of(new College()));
        when(courseRepository.findBySlug("mbbs")).thenReturn(Optional.of(new Course()));
        when(leadRepository.save(any(Lead.class))).thenReturn(leadEntity);
        when(leadMapper.toDTO(leadEntity)).thenReturn(leadDTO);

        LeadDTO result = leadService.submitLead(leadDTO);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john@example.com");
        verify(leadRepository, times(1)).save(any(Lead.class));

        assertThat(meterRegistry.get("business.lead.submitted.total").counter().count()).isEqualTo(1.0);
        assertThat(meterRegistry.get("db.query.lead.save.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should submit lead successfully when optional slug relations are null")
    void testSubmitLead_NullRelations() {
        leadDTO.setCountry(null);
        leadDTO.setUniversity(null);
        leadDTO.setCollege(null);
        leadDTO.setCourse(null);

        when(leadRepository.save(any(Lead.class))).thenReturn(leadEntity);
        when(leadMapper.toDTO(leadEntity)).thenReturn(leadDTO);

        LeadDTO result = leadService.submitLead(leadDTO);

        assertThat(result).isNotNull();
        verify(countryRepository, never()).findBySlug(any());
        verify(universityRepository, never()).findBySlug(any());
        verify(collegeRepository, never()).findBySlug(any());
        verify(courseRepository, never()).findBySlug(any());
        verify(leadRepository, times(1)).save(any(Lead.class));
    }

    @Test
    @DisplayName("Should fetch all leads and record timer")
    void testGetAllLeads() {
        when(leadRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(leadEntity));
        when(leadMapper.toDTO(leadEntity)).thenReturn(leadDTO);

        List<LeadDTO> result = leadService.getAllLeads();

        assertThat(result).isNotNull().hasSize(1);
        verify(leadRepository, times(1)).findAllByOrderByCreatedAtDesc();
        assertThat(meterRegistry.get("db.query.lead.fetch_all.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should fetch leads by status and record timer")
    void testGetLeadsByStatus() {
        when(leadRepository.findByStatusOrderByCreatedAtDesc(LeadStatus.NEW)).thenReturn(List.of(leadEntity));
        when(leadMapper.toDTO(leadEntity)).thenReturn(leadDTO);

        List<LeadDTO> result = leadService.getLeadsByStatus(LeadStatus.NEW);

        assertThat(result).isNotNull().hasSize(1);
        verify(leadRepository, times(1)).findByStatusOrderByCreatedAtDesc(LeadStatus.NEW);
        assertThat(meterRegistry.get("db.query.lead.fetch_by_status.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should fetch lead by email and phone successfully and record timer")
    void testGetLeadByEmailAndPhone_Success() {
        when(leadRepository.findByEmailAndPhone("john@example.com", "9876543210")).thenReturn(Optional.of(leadEntity));
        when(leadMapper.toDTO(leadEntity)).thenReturn(leadDTO);

        LeadDTO result = leadService.getLeadByEmailAndPhone("john@example.com", "9876543210");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john@example.com");
        verify(leadRepository, times(1)).findByEmailAndPhone("john@example.com", "9876543210");
        assertThat(meterRegistry.get("db.query.lead.fetch_by_email_and_phone.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when lead email and phone combination is not found")
    void testGetLeadByEmailAndPhone_NotFound() {
        when(leadRepository.findByEmailAndPhone("missing@example.com", "0000000000")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> leadService.getLeadByEmailAndPhone("missing@example.com", "0000000000"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(leadRepository, times(1)).findByEmailAndPhone("missing@example.com", "0000000000");
    }

    @Test
    @DisplayName("Should update lead status and notes successfully, increment counter, and record timer")
    void testUpdateLeadStatusAndNotes_Success() {
        when(leadRepository.findByEmailAndPhone("john@example.com", "9876543210")).thenReturn(Optional.of(leadEntity));
        when(leadRepository.save(any(Lead.class))).thenReturn(leadEntity);
        when(leadMapper.toDTO(leadEntity)).thenReturn(leadDTO);

        LeadDTO result = leadService.updateLeadStatusAndNotes("john@example.com", "9876543210", LeadStatus.CONTACTED, "Called customer");

        assertThat(result).isNotNull();
        verify(leadRepository, times(1)).findByEmailAndPhone("john@example.com", "9876543210");
        verify(leadRepository, times(1)).save(any(Lead.class));

        assertThat(meterRegistry.get("business.lead.updated.total").counter().count()).isEqualTo(1.0);
        assertThat(meterRegistry.get("db.query.lead.save.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should update lead successfully when status and notes parameters are null")
    void testUpdateLeadStatusAndNotes_NullParams() {
        when(leadRepository.findByEmailAndPhone("john@example.com", "9876543210")).thenReturn(Optional.of(leadEntity));
        when(leadRepository.save(any(Lead.class))).thenReturn(leadEntity);
        when(leadMapper.toDTO(leadEntity)).thenReturn(leadDTO);

        LeadDTO result = leadService.updateLeadStatusAndNotes("john@example.com", "9876543210", null, null);

        assertThat(result).isNotNull();
        verify(leadRepository, times(1)).save(any(Lead.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent lead by email and phone")
    void testUpdateLeadStatusAndNotes_NotFound() {
        when(leadRepository.findByEmailAndPhone("missing@example.com", "0000000000")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> leadService.updateLeadStatusAndNotes("missing@example.com", "0000000000", LeadStatus.CLOSED, "Notes"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(leadRepository, times(1)).findByEmailAndPhone("missing@example.com", "0000000000");
        verify(leadRepository, never()).save(any(Lead.class));
    }

    @Test
    @DisplayName("Should delete lead successfully, increment counter, and record delete timer")
    void testDeleteLead_Success() {
        when(leadRepository.findByEmailAndPhone("john@example.com", "9876543210")).thenReturn(Optional.of(leadEntity));
        doNothing().when(leadRepository).delete(leadEntity);

        leadService.deleteLead("john@example.com", "9876543210");

        verify(leadRepository, times(1)).findByEmailAndPhone("john@example.com", "9876543210");
        verify(leadRepository, times(1)).delete(leadEntity);

        assertThat(meterRegistry.get("business.lead.deleted.total").counter().count()).isEqualTo(1.0);
        assertThat(meterRegistry.get("db.query.lead.delete.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent lead by email and phone")
    void testDeleteLead_NotFound() {
        when(leadRepository.findByEmailAndPhone("missing@example.com", "0000000000")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> leadService.deleteLead("missing@example.com", "0000000000"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(leadRepository, times(1)).findByEmailAndPhone("missing@example.com", "0000000000");
        verify(leadRepository, never()).delete(any());
    }
}