package com.admissionconsultancy.service.impl;

import com.admissionconsultancy.constants.AppCode;
import com.admissionconsultancy.dto.LeadDTO;
import com.admissionconsultancy.entity.Lead;
import com.admissionconsultancy.enums.LeadStatus;
import com.admissionconsultancy.exception.ResourceNotFoundException;
import com.admissionconsultancy.mapper.LeadMapper;
import com.admissionconsultancy.repository.CountryRepository;
import com.admissionconsultancy.repository.CollegeRepository;
import com.admissionconsultancy.repository.CourseRepository;
import com.admissionconsultancy.repository.LeadRepository;
import com.admissionconsultancy.repository.UniversityRepository;
import com.admissionconsultancy.service.LeadService;
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
public class LeadServiceImpl implements LeadService {

    private final LeadRepository leadRepository;
    private final CountryRepository countryRepository;
    private final UniversityRepository universityRepository;
    private final CollegeRepository collegeRepository; // Injected College Repository
    private final CourseRepository courseRepository;
    private final LeadMapper leadMapper;

    // Counters for Business Metrics
    private final Counter leadSubmittedCounter;
    private final Counter leadUpdatedCounter;
    private final Counter leadDeletedCounter;

    // Timers for Database Query Performance
    private final Timer fetchAllQueryTimer;
    private final Timer fetchByStatusQueryTimer;
    private final Timer fetchByEmailAndPhoneQueryTimer;
    private final Timer saveDbQueryTimer;
    private final Timer deleteDbQueryTimer;

    public LeadServiceImpl(
            LeadRepository leadRepository,
            CountryRepository countryRepository,
            UniversityRepository universityRepository,
            CollegeRepository collegeRepository,
            CourseRepository courseRepository,
            LeadMapper leadMapper,
            MeterRegistry meterRegistry) {

        this.leadRepository = leadRepository;
        this.countryRepository = countryRepository;
        this.universityRepository = universityRepository;
        this.collegeRepository = collegeRepository;
        this.courseRepository = courseRepository;
        this.leadMapper = leadMapper;

        // Initialize Business Metric Counters
        this.leadSubmittedCounter = Counter.builder("business.lead.submitted.total")
                .description("Total number of new leads submitted")
                .register(meterRegistry);
        this.leadUpdatedCounter = Counter.builder("business.lead.updated.total")
                .description("Total number of leads updated by admins")
                .register(meterRegistry);
        this.leadDeletedCounter = Counter.builder("business.lead.deleted.total")
                .description("Total number of leads deleted")
                .register(meterRegistry);

        // Initialize DB Performance Timers
        this.fetchAllQueryTimer = Timer.builder("db.query.lead.fetch_all.time")
                .description("Time taken to execute findAll queries on Leads")
                .register(meterRegistry);
        this.fetchByStatusQueryTimer = Timer.builder("db.query.lead.fetch_by_status.time")
                .description("Time taken to fetch leads by specific status")
                .register(meterRegistry);
        this.fetchByEmailAndPhoneQueryTimer = Timer.builder("db.query.lead.fetch_by_email_and_phone.time")
                .description("Time taken to fetch a single lead by email and phone")
                .register(meterRegistry);
        this.saveDbQueryTimer = Timer.builder("db.query.lead.save.time")
                .description("Time taken to persist or update a lead in the database")
                .register(meterRegistry);
        this.deleteDbQueryTimer = Timer.builder("db.query.lead.delete.time")
                .description("Time taken to hard delete a lead from the database")
                .register(meterRegistry);
    }

    @Override
    @Transactional
    public LeadDTO submitLead(LeadDTO leadDTO) {
        log.info("Processing new lead submission for email: {}", leadDTO.getEmail());

        Lead lead = new Lead();
        leadMapper.updateEntityFromDTO(leadDTO, lead);
        lead.setStatus(LeadStatus.NEW); // Ensure it's always NEW upon submission

        // Link Country if slug provided
        if (leadDTO.getCountry() != null && leadDTO.getCountry().getSlug() != null) {
            countryRepository.findBySlug(leadDTO.getCountry().getSlug())
                    .ifPresent(lead::setInterestedCountry);
        }

        // Link University if slug provided
        if (leadDTO.getUniversity() != null && leadDTO.getUniversity().getSlug() != null) {
            universityRepository.findBySlug(leadDTO.getUniversity().getSlug())
                    .ifPresent(lead::setInterestedUniversity);
        }

        // Link College if slug provided
        if (leadDTO.getCollege() != null && leadDTO.getCollege().getSlug() != null) {
            collegeRepository.findBySlug(leadDTO.getCollege().getSlug())
                    .ifPresent(lead::setInterestedCollege);
        }

        // Link Course if slug provided
        if (leadDTO.getCourse() != null && leadDTO.getCourse().getSlug() != null) {
            courseRepository.findBySlug(leadDTO.getCourse().getSlug())
                    .ifPresent(lead::setInterestedCourse);
        }

        // Record Exact DB Insertion Time
        Lead savedLead = saveDbQueryTimer.record(() -> leadRepository.save(lead));

        log.info("Successfully saved new lead for email: {}", savedLead.getEmail());

        // Increment Business Metric
        leadSubmittedCounter.increment();

        return leadMapper.toDTO(savedLead);
    }

    @Override
    public List<LeadDTO> getAllLeads() {
        log.info("Admin: Fetching all leads");

        List<Lead> leads = fetchAllQueryTimer.record(leadRepository::findAllByOrderByCreatedAtDesc);

        return leads.stream()
                .map(leadMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeadDTO> getLeadsByStatus(LeadStatus status) {
        log.info("Admin: Fetching leads with status: {}", status);

        List<Lead> leads = fetchByStatusQueryTimer.record(() ->
                leadRepository.findByStatusOrderByCreatedAtDesc(status));

        return leads.stream()
                .map(leadMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LeadDTO getLeadByEmailAndPhone(String email, String phone) {
        log.info("Admin: Fetching lead with email: {} and phone: {}", email, phone);

        Lead lead = fetchByEmailAndPhoneQueryTimer.record(() -> leadRepository.findByEmailAndPhone(email, phone))
                .orElseThrow(() -> {
                    log.warn("Lead not found with email: {} and phone: {}", email, phone);
                    return new ResourceNotFoundException(AppCode.LEAD_NOT_FOUND.getMessage());
                });

        return leadMapper.toDTO(lead);
    }

    @Override
    @Transactional
    public LeadDTO updateLeadStatusAndNotes(String email, String phone, LeadStatus status, String notes) {
        log.info("Admin: Updating lead with email: {} and phone: {} to status: {}", email, phone, status);

        Lead lead = fetchByEmailAndPhoneQueryTimer.record(() -> leadRepository.findByEmailAndPhone(email, phone))
                .orElseThrow(() -> {
                    log.warn("Update failed. Lead not found with email: {} and phone: {}", email, phone);
                    return new ResourceNotFoundException(AppCode.LEAD_NOT_FOUND.getMessage());
                });

        if (status != null) lead.setStatus(status);
        if (notes != null) lead.setAdminNotes(notes);

        Lead updatedLead = saveDbQueryTimer.record(() -> leadRepository.save(lead));
        leadUpdatedCounter.increment();

        return leadMapper.toDTO(updatedLead);
    }

    @Override
    @Transactional
    public void deleteLead(String email, String phone) {
        log.info("Admin: Deleting lead with email: {} and phone: {}", email, phone);

        Lead lead = leadRepository.findByEmailAndPhone(email, phone)
                .orElseThrow(() -> {
                    log.warn("Delete failed. Lead not found with email: {} and phone: {}", email, phone);
                    throw new ResourceNotFoundException(AppCode.LEAD_NOT_FOUND.getMessage());
                });

        deleteDbQueryTimer.record(() -> leadRepository.delete(lead));
        leadDeletedCounter.increment();
    }
}