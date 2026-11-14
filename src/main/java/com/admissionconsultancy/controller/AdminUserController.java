package com.admissionconsultancy.controller;

import com.admissionconsultancy.dto.ApiResponse;
import com.admissionconsultancy.dto.CreateUserRequestDTO;
import com.admissionconsultancy.dto.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/admin/users")
public interface AdminUserController {

    @PostMapping
    ResponseEntity<ApiResponse<UserResponseDTO>> createUser(@Valid @RequestBody CreateUserRequestDTO request);

    @GetMapping
    ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllUsers();

    @GetMapping("/{email}")
    ResponseEntity<ApiResponse<UserResponseDTO>> getUserByEmail(
            @PathVariable String email);

    @DeleteMapping("/{email}")
    ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable String email);
}