package com.admissionconsultancy.service.impl;

import com.admissionconsultancy.dto.AuthResponseDTO;
import com.admissionconsultancy.dto.LoginRequestDTO;
import com.admissionconsultancy.entity.AdminUser;
import com.admissionconsultancy.exception.UnauthorizedException;
import com.admissionconsultancy.repository.AdminUserRepository;
import com.admissionconsultancy.security.JwtService;
import com.admissionconsultancy.service.AuthService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final AdminUserRepository repository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    // Counters for Security/Business Metrics
    private final Counter loginSuccessCounter;
    private final Counter loginFailedCounter;

    // Timers for Performance Metrics
    private final Timer authenticationTimer;
    private final Timer fetchUserTimer;

    public AuthServiceImpl(
            AdminUserRepository repository,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            MeterRegistry meterRegistry) {

        this.repository = repository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;

        // Initialize Counters
        this.loginSuccessCounter = Counter.builder("business.auth.login.success.total")
                .description("Total number of successful admin logins")
                .register(meterRegistry);

        this.loginFailedCounter = Counter.builder("business.auth.login.failed.total")
                .description("Total number of failed login attempts (bad credentials or inactive)")
                .register(meterRegistry);

        // Initialize Timers
        this.authenticationTimer = Timer.builder("auth.login.authentication_manager.time")
                .description("Time taken to hash and verify BCrypt passwords")
                .register(meterRegistry);

        this.fetchUserTimer = Timer.builder("db.query.admin_user.fetch_by_email.time")
                .description("Time taken to fetch the admin user by email")
                .register(meterRegistry);
    }

    @Override
    public AuthResponseDTO authenticate(LoginRequestDTO request) {
        log.info("Login attempt for email: {}", request.getEmail());

        try {
            // Record Exact Time taken for BCrypt verification
            authenticationTimer.record(() -> {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
                );
            });
        } catch (BadCredentialsException e) {
            log.warn("Failed login attempt for email: {}", request.getEmail());
            // Increment Failed Metric
            loginFailedCounter.increment();
            throw new UnauthorizedException("Invalid email or password");
        } catch (Exception e) {
            loginFailedCounter.increment();
            throw e;
        }

        // Record Exact DB Query Time
        AdminUser user = fetchUserTimer.record(() -> repository.findByEmailAndIsActiveTrue(request.getEmail()))
                .orElseThrow(() -> {
                    // Increment Failed Metric (if user exists in Auth context but not DB/inactive)
                    loginFailedCounter.increment();
                    return new UnauthorizedException("User account is inactive or not found");
                });

        var userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String jwtToken = jwtService.generateToken(userDetails);

        log.info("Successful login for email: {}", request.getEmail());

        // Increment Success Metric
        loginSuccessCounter.increment();

        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(jwtToken);
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRoles(user.getRoles());

        return response;
    }
}