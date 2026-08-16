package com.admissionconsultancy.service;
import com.admissionconsultancy.dto.AuthResponseDTO;
import com.admissionconsultancy.dto.LoginRequestDTO;

public interface AuthService {
    AuthResponseDTO authenticate(LoginRequestDTO request);
}