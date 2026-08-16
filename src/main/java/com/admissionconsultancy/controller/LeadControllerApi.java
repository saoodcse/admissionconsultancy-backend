package com.admissionconsultancy.controller;

import com.admissionconsultancy.dto.ApiResponse;
import com.admissionconsultancy.dto.LeadDTO;
import com.admissionconsultancy.enums.LeadStatus;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
public interface LeadControllerApi {

    // PUBLIC ENDPOINTS (Website forms)

    @PostMapping("/leads")
    ResponseEntity<ApiResponse<LeadDTO>> submitLead(@Valid @RequestBody LeadDTO leadDTO);

    // PROTECTED ADMIN ENDPOINTS (Requires JWT Token)

    @GetMapping("/admin/leads")
    ResponseEntity<ApiResponse<List<LeadDTO>>> getAllLeads(
            @RequestParam(required = false) LeadStatus status
    );

    @GetMapping("/admin/leads/{email}/{phone}")
    ResponseEntity<ApiResponse<LeadDTO>> getLeadByEmailAndPhone(@PathVariable("email") String email, @PathVariable("phone") String phone);

    @PatchMapping("/admin/leads/{email}/{phone}/status")
    ResponseEntity<ApiResponse<LeadDTO>> updateLeadStatus(
            @PathVariable("email") String email,
            @PathVariable("phone") String phone,
            @RequestParam(required = false) LeadStatus status,
            @RequestParam(required = false) String notes
    );

    @DeleteMapping("/admin/leads/{email}/{phone}")
    ResponseEntity<ApiResponse<Void>> deleteLead(@PathVariable("email") String email, @PathVariable("phone") String phone);
}