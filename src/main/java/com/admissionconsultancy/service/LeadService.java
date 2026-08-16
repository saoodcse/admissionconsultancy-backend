package com.admissionconsultancy.service;

import com.admissionconsultancy.dto.LeadDTO;
import com.admissionconsultancy.enums.LeadStatus;

import java.util.List;

public interface LeadService {
    LeadDTO submitLead(LeadDTO leadDTO); // Public method
    List<LeadDTO> getAllLeads(); // Admin method
    List<LeadDTO> getLeadsByStatus(LeadStatus status); // Admin method
    LeadDTO getLeadByEmailAndPhone(String email, String phone); // Admin method
    LeadDTO updateLeadStatusAndNotes(String email, String phone, LeadStatus status, String notes); // Admin method
    void deleteLead(String email, String phone); // Admin method
}