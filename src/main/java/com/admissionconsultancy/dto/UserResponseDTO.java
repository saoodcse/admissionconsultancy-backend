package com.admissionconsultancy.dto;

import com.admissionconsultancy.enums.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserResponseDTO {
    private String name;
    private String email;
    private Set<Role> roles;
    private boolean isActive;
}
