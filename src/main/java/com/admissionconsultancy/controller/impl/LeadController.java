package com.admissionconsultancy.controller.impl;

import com.admissionconsultancy.constants.AppCode;
import com.admissionconsultancy.controller.LeadControllerApi;
import com.admissionconsultancy.dto.ApiResponse;
import com.admissionconsultancy.dto.LeadDTO;
import com.admissionconsultancy.enums.LeadStatus;
import com.admissionconsultancy.service.LeadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LeadController implements LeadControllerApi {

    private final LeadService leadService;

    // PUBLIC ENDPOINTS (Website forms)

    @Override
    public ResponseEntity<ApiResponse<LeadDTO>> submitLead(@RequestBody LeadDTO leadDTO) {
        log.debug("REST request to submit a new lead");
        LeadDTO createdLead = leadService.submitLead(leadDTO);
        return ApiResponse.success(createdLead, AppCode.SUCCESS);
    }

    // PROTECTED ADMIN ENDPOINTS (Requires JWT Token)

    @Override
    public ResponseEntity<ApiResponse<List<LeadDTO>>> getAllLeads(@RequestParam(required = false) LeadStatus status) {
        log.debug("REST request to fetch leads (Admin)");
        List<LeadDTO> leads;

        if (status != null) {
            leads = leadService.getLeadsByStatus(status);
        } else {
            leads = leadService.getAllLeads();
        }

        return ApiResponse.success(leads, AppCode.SUCCESS);
    }

    @Override
    public ResponseEntity<ApiResponse<LeadDTO>> getLeadByEmailAndPhone(@PathVariable("email") String email, @PathVariable("phone") String phone) {
        log.debug("REST request to fetch lead with email: {} and phone: {}", email, phone);
        LeadDTO lead = leadService.getLeadByEmailAndPhone(email, phone);
        return ApiResponse.success(lead, AppCode.SUCCESS);
    }

    @Override
    public ResponseEntity<ApiResponse<LeadDTO>> updateLeadStatus(
            @PathVariable("email") String email,
            @PathVariable("phone") String phone,
            @RequestParam(required = false) LeadStatus status,
            @RequestParam(required = false) String notes) {

        log.debug("REST request to update lead with email: {} and phone: {}", email, phone);
        LeadDTO updatedLead = leadService.updateLeadStatusAndNotes(email, phone, status, notes);
        return ApiResponse.success(updatedLead, AppCode.SUCCESS);
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteLead(@PathVariable("email") String email, @PathVariable("phone") String phone) {
        log.debug("REST request to delete lead with email: {} and phone: {}", email, phone);
        leadService.deleteLead(email, phone);
        return ApiResponse.success(null, AppCode.SUCCESS);
    }
}