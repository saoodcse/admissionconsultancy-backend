package com.admissionconsultancy.service;

import com.admissionconsultancy.dto.CreateUserRequestDTO;
import com.admissionconsultancy.dto.UserResponseDTO;
import java.util.List;

public interface AdminUserService {
    UserResponseDTO createUser(CreateUserRequestDTO request);
    List<UserResponseDTO> getAllActiveUsers();
    UserResponseDTO getUserByEmail(String email);

    void deleteUser(String email);
}