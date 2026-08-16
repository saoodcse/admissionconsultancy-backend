package com.admissionconsultancy.controller;

import com.admissionconsultancy.dto.ApplicationDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Application Management", description = "APIs for managing applications")
@RequestMapping("/api/applications")
public interface ApplicationControllerApi {

    @PostMapping
    ResponseEntity<Void> submitApplication(@RequestBody ApplicationDTO applicationDTO);
}
