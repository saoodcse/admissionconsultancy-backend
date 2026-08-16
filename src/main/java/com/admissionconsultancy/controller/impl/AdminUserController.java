package com.admissionconsultancy.controller.impl;

import com.admissionconsultancy.constants.AppCode;
import com.admissionconsultancy.controller.AdminUserControllerApi;
import com.admissionconsultancy.dto.ApiResponse;
import com.admissionconsultancy.dto.CreateUserRequestDTO;
import com.admissionconsultancy.dto.UserResponseDTO;
import com.admissionconsultancy.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminUserController implements AdminUserControllerApi {

    private final AdminUserService adminUserService;

    @Override
    public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(@RequestBody CreateUserRequestDTO request) {
        log.debug("REST request to create user: {}", request.getEmail());
        UserResponseDTO user = adminUserService.createUser(request);
        return ApiResponse.success(user, AppCode.SUCCESS);
    }

    @Override
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllUsers() {
        log.debug("REST request to fetch all users");
        return ApiResponse.success(adminUserService.getAllActiveUsers(), AppCode.SUCCESS);
    }

    @Override
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserByEmail(String email) {
        log.debug("REST request to fetch user: {}", email);
        return ApiResponse.success(
                adminUserService.getUserByEmail(email),
                AppCode.SUCCESS
        );
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteUser(String email) {
        log.debug("REST request to delete user: {}", email);
        adminUserService.deleteUser(email);
        return ApiResponse.success(null, AppCode.SUCCESS);
    }
}