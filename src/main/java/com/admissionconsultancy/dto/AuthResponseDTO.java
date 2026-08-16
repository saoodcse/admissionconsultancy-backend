package com.admissionconsultancy.dto;

import com.admissionconsultancy.enums.Role;
import lombok.Data;

import java.util.Set;

@Data
public class AuthResponseDTO {
    private String token;
    private String name;
    private String email;
    private Set<Role> roles;
}