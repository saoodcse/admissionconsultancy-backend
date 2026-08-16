package com.admissionconsultancy.service.impl;

import com.admissionconsultancy.dto.AuthResponseDTO;
import com.admissionconsultancy.dto.LoginRequestDTO;
import com.admissionconsultancy.entity.AdminUser;
import com.admissionconsultancy.enums.Role;
import com.admissionconsultancy.exception.UnauthorizedException;
import com.admissionconsultancy.repository.AdminUserRepository;
import com.admissionconsultancy.security.JwtService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AdminUserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    private MeterRegistry meterRegistry;
    private AuthServiceImpl authService;

    private LoginRequestDTO loginRequest;
    private AdminUser adminUser;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        authService = new AuthServiceImpl(
                userRepository,
                jwtService,
                authenticationManager,
                userDetailsService,
                meterRegistry
        );

        loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("admin@admission.com");
        loginRequest.setPassword("securePassword123");

        adminUser = new AdminUser();
        adminUser.setId(1L);
        adminUser.setName("Admin User");
        adminUser.setEmail("admin@admission.com");
        adminUser.setRoles(Set.of(Role.ADMIN));
        adminUser.setActive(true);

        userDetails = new User("admin@admission.com", "securePassword123", Collections.emptyList());
    }

    @Test
    @DisplayName("Should authenticate successfully, generate JWT, increment success counter, and record timers")
    void testAuthenticate_Success() {
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmailAndIsActiveTrue("admin@admission.com")).thenReturn(Optional.of(adminUser));
        when(userDetailsService.loadUserByUsername("admin@admission.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("mock_jwt_token");

        AuthResponseDTO response = authService.authenticate(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mock_jwt_token");
        assertThat(response.getEmail()).isEqualTo("admin@admission.com");
        assertThat(response.getRoles()).contains(Role.ADMIN);

        verify(authenticationManager, times(1)).authenticate(any());
        verify(userRepository, times(1)).findByEmailAndIsActiveTrue("admin@admission.com");
        verify(userDetailsService, times(1)).loadUserByUsername("admin@admission.com");
        verify(jwtService, times(1)).generateToken(userDetails);

        // Verify Micrometer Metrics
        assertThat(meterRegistry.get("business.auth.login.success.total").counter().count()).isEqualTo(1.0);
        assertThat(meterRegistry.get("auth.login.authentication_manager.time").timer().count()).isEqualTo(1);
        assertThat(meterRegistry.get("db.query.admin_user.fetch_by_email.time").timer().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw UnauthorizedException and increment failed counter when credentials are bad")
    void testAuthenticate_BadCredentials() {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.authenticate(loginRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid email or password");

        verify(authenticationManager, times(1)).authenticate(any());
        verify(userRepository, never()).findByEmailAndIsActiveTrue(any());

        assertThat(meterRegistry.get("business.auth.login.failed.total").counter().count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should throw UnauthorizedException and increment failed counter when user is inactive or not found")
    void testAuthenticate_UserNotFoundOrInactive() {
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmailAndIsActiveTrue("admin@admission.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.authenticate(loginRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("User account is inactive or not found");

        verify(authenticationManager, times(1)).authenticate(any());
        verify(userRepository, times(1)).findByEmailAndIsActiveTrue("admin@admission.com");

        assertThat(meterRegistry.get("business.auth.login.failed.total").counter().count()).isEqualTo(1.0);
    }
}