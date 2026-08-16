package com.admissionconsultancy.controller.impl;

import com.admissionconsultancy.constants.AppCode;
import com.admissionconsultancy.controller.AuthControllerApi;
import com.admissionconsultancy.dto.ApiResponse;
import com.admissionconsultancy.dto.AuthResponseDTO;
import com.admissionconsultancy.dto.LoginRequestDTO;
import com.admissionconsultancy.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController implements AuthControllerApi {

    private final AuthService authService;

    @Override
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@RequestBody LoginRequestDTO request) {
        log.debug("REST request to authenticate user: {}", request.getEmail());
        AuthResponseDTO authResponse = authService.authenticate(request);
        return ApiResponse.success(authResponse, AppCode.SUCCESS);
    }
}