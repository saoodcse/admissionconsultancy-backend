package com.admissionconsultancy.service.impl;

import com.admissionconsultancy.dto.CreateUserRequestDTO;
import com.admissionconsultancy.dto.UserResponseDTO;
import com.admissionconsultancy.entity.AdminUser;
import com.admissionconsultancy.enums.Role;
import com.admissionconsultancy.exception.DuplicateResourceException;
import com.admissionconsultancy.repository.AdminUserRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceImplTest {

    @Mock
    private AdminUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private MeterRegistry meterRegistry;
    private AdminUserServiceImpl adminUserService;

    private AdminUser activeUserEntity;
    private AdminUser inactiveUserEntity;
    private CreateUserRequestDTO createUserRequestDTO;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        adminUserService = new AdminUserServiceImpl(userRepository, passwordEncoder, meterRegistry);

        activeUserEntity = new AdminUser();
        activeUserEntity.setId(1L);
        activeUserEntity.setName("Admin User");
        activeUserEntity.setEmail("admin@admission.com");
        activeUserEntity.setPassword("encoded_pass");
        activeUserEntity.setRoles(Set.of(Role.ADMIN));
        activeUserEntity.setActive(true);

        inactiveUserEntity = new AdminUser();
        inactiveUserEntity.setId(2L);
        inactiveUserEntity.setName("Inactive User");
        inactiveUserEntity.setEmail("inactive@admission.com");
        inactiveUserEntity.setPassword("encoded_pass");
        inactiveUserEntity.setRoles(Set.of(Role.VIEWER));
        inactiveUserEntity.setActive(false);

        createUserRequestDTO = new CreateUserRequestDTO();
        createUserRequestDTO.setName("Admin User");
        createUserRequestDTO.setEmail("admin@admission.com");
        createUserRequestDTO.setPassword("rawPassword123");
        createUserRequestDTO.setRoles(Set.of(Role.ADMIN));
    }

    @Test
    @DisplayName("Should create admin user successfully, encode password, increment counter, and record timers")
    void testCreateUser_Success() {
        when(userRepository.existsByEmail("admin@admission.com")).thenReturn(false);
        when(passwordEncoder.encode("rawPassword123")).thenReturn("encoded_pass");
        when(userRepository.save(any(AdminUser.class))).thenReturn(activeUserEntity);

        UserResponseDTO result = adminUserService.createUser(createUserRequestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("admin@admission.com");
        assertThat(result.getRoles()).contains(Role.ADMIN);

        verify(userRepository, times(1)).existsByEmail("admin@admission.com");
        verify(passwordEncoder, times(1)).encode("rawPassword123");
        verify(userRepository, times(1)).save(any(AdminUser.class));

        // Verify Micrometer Metrics
        assertThat(meterRegistry.get("business.admin_user.created.total").counter().count()).isEqualTo(1.0);
        assertThat(meterRegistry.get("db.query.admin_user.exists_by_email.time").timer().count()).isEqualTo(1);
        assertThat(meterRegistry.get("auth.password.encode.time").timer().count()).isEqualTo(1);
        assertThat(meterRegistry.get("db.query.admin_user.save.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when email already exists during creation")
    void testCreateUser_DuplicateEmail() {
        when(userRepository.existsByEmail("admin@admission.com")).thenReturn(true);

        assertThatThrownBy(() -> adminUserService.createUser(createUserRequestDTO))
                .isInstanceOf(DuplicateResourceException.class);

        verify(userRepository, times(1)).existsByEmail("admin@admission.com");
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());

        assertThat(meterRegistry.get("business.admin_user.created.total").counter().count()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should fetch all active users and filter out inactive ones, recording timer")
    void testGetAllActiveUsers() {
        when(userRepository.findAll()).thenReturn(List.of(activeUserEntity, inactiveUserEntity));

        List<UserResponseDTO> result = adminUserService.getAllActiveUsers();

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0.0 > -1 ? 0 : 0).getEmail()).isEqualTo("admin@admission.com");

        verify(userRepository, times(1)).findAll();
        assertThat(meterRegistry.get("db.query.admin_user.fetch_all.time").timer().count()).isEqualTo(1);
    }
}